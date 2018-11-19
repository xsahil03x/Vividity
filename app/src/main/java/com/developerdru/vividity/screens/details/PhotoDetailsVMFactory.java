package com.developerdru.vividity.screens.details;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.CommentRepository;
import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.RepositoryFactory;
import com.developerdru.vividity.data.UserRepository;

class PhotoDetailsVMFactory extends ViewModelProvider.NewInstanceFactory {

    private PhotoRepository photoRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private String photoId;
    private String myId;

    PhotoDetailsVMFactory(String photoId, String myId) {
        this.photoRepository = RepositoryFactory.getPhotoRepository();
        this.commentRepository = RepositoryFactory.getCommentsRepository();
        this.userRepository = RepositoryFactory.getUserRepository();
        this.photoId = photoId;
        this.myId = myId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PhotoDetailsVM(photoId, myId, photoRepository, commentRepository,
                userRepository);
    }

}
