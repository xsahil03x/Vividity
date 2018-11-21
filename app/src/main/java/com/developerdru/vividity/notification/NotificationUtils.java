package com.developerdru.vividity.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;

import com.developerdru.vividity.R;

import java.util.Locale;

public class NotificationUtils {

    private static final String DEFAULT_CHANNEL_ID = "channelDefault";

    public static void registerNotificationChannels(@NonNull Context cxt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = cxt.getSystemService(NotificationManager
                    .class);

            CharSequence nameDefault = cxt.getString(R.string.notif_channel_default);
            String defaultDescription = cxt.getString(R.string.channel_default_description);
            NotificationChannel channelDefault = new NotificationChannel(DEFAULT_CHANNEL_ID,
                    nameDefault,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channelDefault.setDescription(defaultDescription);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channelDefault);
            }
        }
    }

    public static void showNewPicNotification(@NonNull Context cxt, @NonNull Intent
            intentToOpen, String uploaderName, String message, String ticker) {

        String title = String.format(Locale.US, cxt.getString(R.string
                .msg_new_pic_by_people_you_follow), uploaderName);

        int notificationId = (int) System.currentTimeMillis();

        NotificationCompat.Builder builder = (new NotificationCompat.Builder(cxt,
                DEFAULT_CHANNEL_ID)).setSmallIcon(R.drawable.ic_baby_mono)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(PendingIntent.getActivity(cxt, 0, intentToOpen, 0))
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(ticker)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cxt);
        notificationManager.notify(notificationId, builder.build());
    }

    public static void showDownloadedNotification(@NonNull Context cxt, @Nullable Intent
            intentToOpen) {

        String title = cxt.getString(R.string.msg_download_complete);
        String contentText = cxt.getString(R.string.msg_downloaded_tap_to_view);

        int notificationId = (int) System.currentTimeMillis();

        PendingIntent pi = null;
        if (intentToOpen != null) {
            pi = PendingIntent.getActivity(cxt, 0, intentToOpen, 0);
        }

        NotificationCompat.Builder builder = (new NotificationCompat.Builder(cxt,
                DEFAULT_CHANNEL_ID)).setSmallIcon(R.drawable.ic_file_download)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(pi)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cxt);
        notificationManager.notify(notificationId, builder.build());
    }
}
