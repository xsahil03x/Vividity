package com.developerdru.vividity.data;

import android.arch.lifecycle.LiveData;

import com.developerdru.vividity.data.entities.PhotoComment;
import com.developerdru.vividity.data.remote.OperationStatus;

import java.util.List;

public interface CommentRepository {

    LiveData<List<PhotoComment>> getCommentsForPhoto(String photoId);

    void deleteComment(String photoId, String commentId);
}
