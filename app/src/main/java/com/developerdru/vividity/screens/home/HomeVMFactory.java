package com.developerdru.vividity.screens.home;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.RepositoryFactory;

public class HomeVMFactory extends ViewModelProvider.NewInstanceFactory {

    private PhotoRepository photoRepository;
    private int sortBy;
    private long startAt;
    private int limit;

    HomeVMFactory(@HomeVM.OrderByParams int sortBy, long startAt, int limit) {
        this.photoRepository = RepositoryFactory.getPhotoRepository();
        this.sortBy = sortBy;
        this.startAt = startAt;
        this.limit = limit;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HomeVM(photoRepository, sortBy, startAt, limit);
    }
}
