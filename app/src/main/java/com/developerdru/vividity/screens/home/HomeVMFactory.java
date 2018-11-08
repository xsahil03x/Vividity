package com.developerdru.vividity.screens.home;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.RepositoryFactory;
import com.developerdru.vividity.data.UserRepository;

public class HomeVMFactory extends ViewModelProvider.NewInstanceFactory {

    private PhotoRepository photoRepository;
    private UserRepository userRepository;
    private String myId;
    private int sortBy;

    HomeVMFactory(@HomeVM.OrderByParams int sortBy, String myId) {
        this.photoRepository = RepositoryFactory.getPhotoRepository();
        this.userRepository = RepositoryFactory.getUserRepository();
        this.sortBy = sortBy;
        this.myId = myId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HomeVM(photoRepository, userRepository, myId, sortBy);
    }
}
