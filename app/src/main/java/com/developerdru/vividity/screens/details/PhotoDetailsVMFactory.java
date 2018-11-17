package com.developerdru.vividity.screens.details;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.CommentRepository;
import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.RepositoryFactory;

class PhotoDetailsVMFactory extends ViewModelProvider.NewInstanceFactory {

    private PhotoRepository photoRepository;
    private CommentRepository commentRepository;
    private String photoId;

    PhotoDetailsVMFactory(String photoId) {
        this.photoRepository = RepositoryFactory.getPhotoRepository();
        this.commentRepository = RepositoryFactory.getCommentsRepository();
        this.photoId = photoId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PhotoDetailsVM(photoId, photoRepository, commentRepository);
    }

}
