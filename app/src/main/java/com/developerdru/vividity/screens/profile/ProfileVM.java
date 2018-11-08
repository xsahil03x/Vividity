package com.developerdru.vividity.screens.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.UserRepository;
import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.data.entities.User;
import com.developerdru.vividity.data.remote.OperationStatus;

import java.util.List;

class ProfileVM extends ViewModel {

    private LiveData<User> userData;

    private LiveData<List<FollowUser>> follows;

    private UserRepository userRepository;

    ProfileVM(String userId, @NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userData = this.userRepository.getUserInfo(userId);
        this.follows = userRepository.getFollowsList(userId);
    }

    LiveData<User> getUserData() {
        return userData;
    }

    LiveData<List<FollowUser>> getMyFollows() {
        return follows;
    }

    LiveData<OperationStatus> changeUserNotificationStatus(@NonNull String userId, boolean
            getNotified) {
        return userRepository.updateNotificationSetting(userId, getNotified);
    }

    LiveData<OperationStatus> unfollowUser(@NonNull String userId) {
        return userRepository.unFollowUser(userId);
    }
}
