package com.developerdru.vividity.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.developerdru.vividity.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    private static final String PROVIDER_GOOGLE = "google.com";
    private static final String PROVIDER_TWITTER = "twitter.com";

    private static final String MIME_IMAGE = "image/*";

    public static void emailFeedbackIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kaushal.devil009@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Vividity feedback - " + System.currentTimeMillis());
        context.startActivity(intent);
    }

    @DrawableRes
    public static int getSignInServiceDrawable(String signInService) {
        switch (signInService) {
            case PROVIDER_GOOGLE:
                return R.drawable.ic_google;
            case PROVIDER_TWITTER:
                return R.drawable.ic_twitter;
            default:
                return R.drawable.ic_firebase;
        }
    }

    public static Uri getFileProviderUri(@NonNull Context appContext, @NonNull File localFile) {
        appContext = appContext.getApplicationContext();
        String provider = appContext.getPackageName() + ".provider";
        return FileProvider.getUriForFile(appContext, provider, localFile);
    }

    public static AlertDialog getImageSrcSelectionDialog(final Activity activity, String
            cameraFileName, final int galleryCode, final int cameraCode) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(activity.getString(R.string.title_add_pic));
        String[] types = {activity.getString(R.string.camera_capture), activity.getString(R.string
                .pick_from_galery)};
        alertDialog.setItems(types, (dialog, which) -> {

            dialog.dismiss();

            switch (which) {
                // Camera selected
                case 0:
                    File imageFile = null;
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                        try {
                            imageFile = new File(activity.getExternalFilesDir(Environment
                                    .DIRECTORY_PICTURES), cameraFileName + ".jpg");

                            Uri photoUri = getFileProviderUri(activity, imageFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            activity.startActivityForResult(cameraIntent, cameraCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("vividity", "No camera support found");
                        Toast.makeText(activity, R.string.no_camera_found, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                // Picking image from gallery
                case 1:
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType(MIME_IMAGE);
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(galleryIntent,
                            activity.getString(R.string.pick_from_gallery_title)), galleryCode);
                    break;
            }
        });

        return (alertDialog.create());

    }

}
