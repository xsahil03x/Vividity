package com.developerdru.vividity.data;

import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.data.entities.PhotoComment;

import java.util.List;

/**
 * Defines signature for various types of data sources
 */
public interface DataSource {

    interface Photo {
        LiveData<com.developerdru.vividity.data.entities.Photo> getPhotos();

        LiveData<List<PhotoComment>> getCommentsForPhoto(String photoId);
    }

    interface User {
        LiveData<com.developerdru.vividity.data.entities.User> getUserInfo(String userId);

        LiveData<FollowUser> getFollowerList(String userId);

        LiveData<FollowUser> getFollowsList(String userId);

        void followUser(@NonNull String userId, @NonNull String userName, @NonNull String
                profilePic, @NonNull Listener listener);

        void unFollowUser(@NonNull String userId, @NonNull Listener listener);

        void updateNotificationSetting(@NonNull String userId, boolean follow, @NonNull Listener
                listener);

        void updateMyInfo(@Nullable String displayName, @Nullable String profilePicName,
                          @Nullable String profilePicUrl, @NonNull Listener listener);

        interface Listener {
            void onComplete();

            void onError();
        }
    }

    interface Storage {
        void uploadProfilePic(Uri localProfilePicUri, Listener listener);

        void uploadPhoto(@NonNull Uri localPhotoUri, @NonNull String fileName, @NonNull Listener
                listener);

        interface Listener {
            void onComplete(String downloadURL, String name);

            void onError(String errorMessage);

            void onProgressUpdate(long uploadedByteCount, long maxSizeCount);
        }
    }

}
