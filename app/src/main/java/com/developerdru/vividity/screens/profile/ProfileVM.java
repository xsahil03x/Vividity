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

    private String myId;

    ProfileVM(String userId, String myId, @NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userData = this.userRepository.getUserInfo(userId);
        this.follows = userRepository.getFollowsList(userId);
        LiveData<List<FollowUser>> myFollows = userRepository.getFollowsList(myId);
        this.myId = myId;
    }

    LiveData<User> getUserData() {
        return userData;
    }

    LiveData<List<FollowUser>> getMyFollows() {
        return follows;
    }

    LiveData<Boolean> amIFollowing(String userIdToCheck) {
        return userRepository.checkFollowStatus(userIdToCheck, myId);
    }

    LiveData<OperationStatus> changeUserNotificationStatus(@NonNull String userId, boolean
            getNotified) {
        return userRepository.updateNotificationSetting(userId, getNotified);
    }

    LiveData<OperationStatus> unfollowUser(@NonNull String userId) {
        return userRepository.unFollowUser(userId);
    }

    LiveData<OperationStatus> followUser(String userId, String userName, String profilePic) {
        return userRepository.followUser(userId, userName, profilePic);
    }
}
