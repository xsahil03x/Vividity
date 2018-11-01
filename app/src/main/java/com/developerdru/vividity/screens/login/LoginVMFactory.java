package com.developerdru.vividity.screens.login;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.RepositoryFactory;
import com.developerdru.vividity.data.UserRepository;

public class LoginVMFactory extends ViewModelProvider.NewInstanceFactory {

    private UserRepository userRepository;

    LoginVMFactory() {
        userRepository = RepositoryFactory.getUserRepository();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoginVM(userRepository);
    }
}
