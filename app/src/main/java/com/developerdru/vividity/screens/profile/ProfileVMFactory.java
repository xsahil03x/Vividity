package com.developerdru.vividity.screens.profile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.RepositoryFactory;
import com.developerdru.vividity.data.UserRepository;

public class ProfileVMFactory extends ViewModelProvider.NewInstanceFactory {

    private UserRepository userRepository;
    private String userId;
    private String myId;

    ProfileVMFactory(String userId, String myId) {
        this.userRepository = RepositoryFactory.getUserRepository();
        this.userId = userId;
        this.myId = myId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ProfileVM(userId, myId, userRepository);
    }
}
