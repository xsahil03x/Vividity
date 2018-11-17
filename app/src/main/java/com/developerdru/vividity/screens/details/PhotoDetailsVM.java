package com.developerdru.vividity.screens.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.CommentRepository;
import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.data.entities.PhotoComment;

import java.util.List;

class PhotoDetailsVM extends ViewModel {

    private LiveData<Photo> photoLiveData;
    private LiveData<List<PhotoComment>> commentLiveData;

    private CommentRepository commentRepository;
    private String photoId;

    PhotoDetailsVM(@NonNull String photoId, @NonNull PhotoRepository photoRepository, @NonNull
            CommentRepository commentRepository) {
        this.photoId = photoId;
        this.commentRepository = commentRepository;
        photoLiveData = photoRepository.getPhotoDetails(photoId);
        commentLiveData = commentRepository.getCommentsForPhoto(photoId);
    }

    LiveData<Photo> getPhotoMetadata() {
        return photoLiveData;
    }

    LiveData<List<PhotoComment>> getCommentLiveData() {
        return commentLiveData;
    }

    void deleteComment(PhotoComment comment) {
        commentRepository.deleteComment(photoId, comment.getCommentIdentifier());
    }
}
