package com.developerdru.vividity.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.data.entities.User;
import com.developerdru.vividity.data.remote.OperationStatus;

/**
 * Defines signature for various types of data sources
 */
public interface UserRepository {

    LiveData<User> getUserInfo(String userId);

    LiveData<FollowUser> getFollowerList(String userId);

    LiveData<FollowUser> getFollowsList(String userId);

    LiveData<OperationStatus> followUser(@NonNull String userId, @NonNull String userName,
                                         @NonNull String profilePic);

    LiveData<OperationStatus> unFollowUser(@NonNull String userId);

    LiveData<OperationStatus> updateNotificationSetting(@NonNull String userId, boolean follow);

    LiveData<OperationStatus> updateMyInfo(String userId, String provider, String
            profilePic, String providerIdentifier, String displayName, String fcmToken);

    LiveData<OperationStatus> updateMyFCMToken(@NonNull String fcmToken);
}
