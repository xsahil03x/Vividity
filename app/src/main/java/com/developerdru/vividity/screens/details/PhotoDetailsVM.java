package com.developerdru.vividity.screens.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.CommentRepository;
import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.UserRepository;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.data.entities.PhotoComment;
import com.developerdru.vividity.data.remote.OperationStatus;

import java.util.List;

class PhotoDetailsVM extends ViewModel {

    private LiveData<Photo> photoLiveData;
    private LiveData<List<PhotoComment>> commentLiveData;

    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private PhotoRepository photoRepository;
    private String photoId;
    private String myId;

    PhotoDetailsVM(@NonNull String photoId, @NonNull String myId, @NonNull PhotoRepository
            photoRepository, @NonNull CommentRepository commentRepository, UserRepository
                           userRepository) {
        this.photoId = photoId;
        this.myId = myId;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        photoLiveData = photoRepository.getPhotoDetails(photoId);
        commentLiveData = commentRepository.getCommentsForPhoto(photoId);
    }

    LiveData<Photo> getPhotoMetadata() {
        return photoLiveData;
    }

    LiveData<List<PhotoComment>> getCommentLiveData() {
        return commentLiveData;
    }

    LiveData<Boolean> amIFollowing(String userIdToCheck) {
        return userRepository.checkFollowStatus(userIdToCheck, myId);
    }

    LiveData<OperationStatus> incrementUpvoteCount() {
        return photoRepository.incrementUpvoteCount(photoId);
    }

    LiveData<OperationStatus> addComment(String commentText) {
        return commentRepository.addComment(photoId, commentText, myId);
    }

    void deleteComment(PhotoComment comment) {
        commentRepository.deleteComment(photoId, comment.getCommentIdentifier());
    }
}
