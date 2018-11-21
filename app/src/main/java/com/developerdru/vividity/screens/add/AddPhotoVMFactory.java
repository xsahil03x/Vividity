package com.developerdru.vividity.screens.add;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.RepositoryFactory;
import com.developerdru.vividity.data.StorageRepository;

class AddPhotoVMFactory extends ViewModelProvider.NewInstanceFactory {

    private StorageRepository storageRepository;
    private PhotoRepository photoRepository;

    AddPhotoVMFactory() {
        storageRepository = RepositoryFactory.getStorageRepository();
        photoRepository = RepositoryFactory.getPhotoRepository();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddPhotoVM(storageRepository, photoRepository);
    }
}
