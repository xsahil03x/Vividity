package com.developerdru.vividity.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.developerdru.vividity.R;
import com.developerdru.vividity.notification.NotificationUtils;
import com.developerdru.vividity.utils.Utility;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Downloader extends IntentService {

    private static final int CHUNK_SIZE = 1024;

    private static final String ACTION_DOWNLOAD = "DOWNLOAD";
    private static final String EXTRA_NAME = "filename";
    private static final String EXTRA_URL = "url";
    private static final String EXTRA_SHARE = "share";

    private static final String TAG = "DOWNLOADER";
    private static final String APP_DOWNLOADS_FOLDER = "Vividity";

    private final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

    public Downloader() {
        super("Downloader");
    }

    public static void enqueue(@NonNull Context context, @NonNull String fileName, String url,
                               boolean shareAfterDownload) {
        Intent intent = new Intent(context, Downloader.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_NAME, fileName);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_SHARE, shareAfterDownload);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String fileName = intent.getStringExtra(EXTRA_NAME);
                final String downloadURL = intent.getStringExtra(EXTRA_URL);
                final boolean shareAfterDownload = intent.getBooleanExtra(EXTRA_SHARE, false);
                handleDownload(fileName, downloadURL, shareAfterDownload);
            } else {
                Crashlytics.log("Unknown action in Downloader service: " + action);
            }
        }
    }

    /**
     * Download the file and save locally. Launch share intent based on passed parameter
     *
     * @param fileName           Name of the file
     * @param downloadURL        download URL of the file
     * @param shareAfterDownload launch share intent after download if true, ignore otherwise
     */
    private void handleDownload(String fileName, String downloadURL, boolean shareAfterDownload) {
        File destFolder = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_DOWNLOADS), APP_DOWNLOADS_FOLDER);

        Handler mainHandler = new Handler(Looper.getMainLooper());

        if (!destFolder.exists()) {
            boolean created = destFolder.mkdirs();
            if (!created) {
                Crashlytics.log("destination folder could not be created");
                mainHandler.post(() -> Toast.makeText(this.getApplicationContext(), R.string
                        .something_went_wrong, Toast.LENGTH_SHORT).show());
                return;
            }
        }

        File destFile = new File(destFolder, fileName);

        DataInputStream dis = null;
        FileOutputStream fos = null;

        try {

            URL u = new URL(downloadURL);
            InputStream is = u.openStream();

            dis = new DataInputStream(is);

            byte[] buffer = new byte[CHUNK_SIZE];
            int length;

            fos = new FileOutputStream(destFile);
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            mainHandler.post(() -> Toast.makeText(this.getApplicationContext(), R.string
                    .msg_download_complete, Toast.LENGTH_SHORT).show());

            if (shareAfterDownload) {
                handleShare(destFile);
            } else {
                Uri downloadedFileURI = Utility.getFileProviderUri(getApplicationContext(),
                        destFile);
                Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                openFileIntent.setData(downloadedFileURI);
                if (openFileIntent.resolveActivity(getPackageManager()) != null) {
                    NotificationUtils.showDownloadedNotification(getApplicationContext(),
                            openFileIntent);
                } else {
                    NotificationUtils.showDownloadedNotification(getApplicationContext(), null);
                }
            }

        } catch (MalformedURLException mue) {
            Log.e(TAG, "Malformed: " + mue.toString());
            mue.printStackTrace();
            Crashlytics.logException(mue);
        } catch (IOException ioe) {
            Log.e(TAG, "IOException: " + ioe.toString());
            ioe.printStackTrace();
            Crashlytics.logException(ioe);
        } catch (Exception ex) {
            Log.e(TAG, "Security: " + ex.toString());
            ex.printStackTrace();
            Crashlytics.logException(ex);
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleShare(@NonNull File destFile) throws FileNotFoundException {
        MediaStore.Images.Media.insertImage(getContentResolver(), destFile.getAbsolutePath(),
                "", null);
        Uri fileUri = Utility.getFileProviderUri(getApplicationContext(), destFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_via_vividity));

        shareIntent.setType(mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
                .getFileExtensionFromUrl(destFile.getAbsolutePath())));

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string
                    .text_share_now));
            chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(chooserIntent);
        } else {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> Toast.makeText(this.getApplicationContext(), R.string
                    .text_unable_to_share, Toast.LENGTH_SHORT).show());
        }
    }
}
