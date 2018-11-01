package com.developerdru.vividity.screens.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.UserRepository;
import com.developerdru.vividity.data.remote.OperationStatus;

class LoginVM extends ViewModel {

    private LiveData<OperationStatus> statusLiveData;

    private UserRepository repository;

    LoginVM(UserRepository userRepository) {
        this.repository = userRepository;
    }

    LiveData<OperationStatus> updateCurrentUserInfo(
            @NonNull String userId, String provider, String profilePic, String
            providerIdentifier, String displayName, String fcmToken) {

        return repository.updateMyInfo(userId, provider, profilePic, providerIdentifier,
                displayName, fcmToken);
    }

}
