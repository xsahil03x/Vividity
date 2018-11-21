package com.developerdru.vividity.services;

import com.crashlytics.android.Crashlytics;
import com.developerdru.vividity.data.RepositoryFactory;
import com.developerdru.vividity.notification.NotificationUtils;
import com.developerdru.vividity.screens.details.PhotoDetailsScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String KEY_TICKER = "ticker";
    private static final String KEY_PHOTO_ID = "photoId";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_UPLOADER_ID = "uploaderId";
    private static final String KEY_UPLOADER_NAME = "uploader";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            // This push message hai data payload
            Map<String, String> receivedMap = remoteMessage.getData();

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                // User has not logged in, ignore the message
                return;
            }

            String ticker = receivedMap.get(KEY_TICKER);
            String message = receivedMap.get(KEY_MESSAGE);
            String photoId = receivedMap.get(KEY_PHOTO_ID);
            String uploaderId = receivedMap.get(KEY_UPLOADER_ID);
            String uploaderName = receivedMap.get(KEY_UPLOADER_NAME);

            if (photoId != null) {
                NotificationUtils.showNewPicNotification(getApplicationContext(),
                        PhotoDetailsScreen.getLaunchIntent(getApplicationContext(), photoId),
                        uploaderName, message, ticker);
            }

        }

        if (remoteMessage.getNotification() != null) {
            // Not handling Notification payload yet
            Crashlytics.log("Notification payload received");
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        RepositoryFactory.getUserRepository().updateMyFCMToken(s);
    }
}
