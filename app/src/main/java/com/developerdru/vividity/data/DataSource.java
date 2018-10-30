package com.developerdru.vividity.data;

import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.data.remote.OperationStatus;
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

        LiveData<OperationStatus> followUser(@NonNull String userId, @NonNull String userName,
                                             @NonNull String profilePic);

        LiveData<OperationStatus> unFollowUser(@NonNull String userId);

        LiveData<OperationStatus> updateNotificationSetting(@NonNull String userId, boolean follow);

        LiveData<OperationStatus> updateMyInfo(@Nullable String displayName, @Nullable String
                profilePicName, @Nullable String profilePicUrl);
    }

    interface Storage {
        LiveData<OperationStatus> uploadProfilePic(Uri localProfilePicUri);

        LiveData<OperationStatus> uploadPhoto(@NonNull Uri localPhotoUri, @NonNull String fileName);

        LiveData<OperationStatus> downloadPhoto(@NonNull String storagePath, @NonNull Uri
                localPhotoUri);
    }

}
