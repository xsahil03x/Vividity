package com.developerdru.vividity.screens.profile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.RepositoryFactory;
import com.developerdru.vividity.data.UserRepository;

public class ProfileVMFactory extends ViewModelProvider.NewInstanceFactory {

    private UserRepository userRepository;
    private String userId;

    public ProfileVMFactory(String userId) {
        this.userRepository = RepositoryFactory.getUserRepository();
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ProfileVM(userId, userRepository);
    }
}
