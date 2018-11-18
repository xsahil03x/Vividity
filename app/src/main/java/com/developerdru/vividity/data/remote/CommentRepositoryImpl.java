package com.developerdru.vividity.data.remote;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.developerdru.vividity.data.CommentRepository;
import com.developerdru.vividity.data.entities.PhotoComment;
import com.developerdru.vividity.utils.FirebasePaths;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommentRepositoryImpl implements CommentRepository {

    private static final String TAG = "PhotoRepositoryImpl";

    private static CommentRepositoryImpl INSTANCE;

    public synchronized static CommentRepositoryImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CommentRepositoryImpl();
        }
        return INSTANCE;
    }

    private DatabaseReference commentsRef;


    private CommentRepositoryImpl() {
        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        commentsRef = baseRef.child(FirebasePaths.COMMENTS_DB_PATH);
    }

    @Override
    public LiveData<List<PhotoComment>> getCommentsForPhoto(String photoId) {
        FirebaseQueryLiveData commentsData = new FirebaseQueryLiveData(commentsRef.child(photoId));
        return Transformations.map(commentsData, input -> {
            GenericTypeIndicator<Map<String, PhotoComment>> typeIndicator = new
                    GenericTypeIndicator<Map<String, PhotoComment>>() {
                    };
            Map<String, PhotoComment> photoCommentMap = input.getValue(typeIndicator);
            if (photoCommentMap != null) {
                for (Map.Entry<String, PhotoComment> item : photoCommentMap.entrySet()) {
                    item.getValue().setCommentIdentifier(item.getKey());
                }
            }
            List<PhotoComment> comments = null;
            if (photoCommentMap != null) {
                comments = new ArrayList<>(photoCommentMap.values());
                Collections.sort(comments, (o1, o2) ->
                        (int) (o1.getTimestamp() - o2.getTimestamp()));
            }
            return comments;
        });
    }

    @Override
    public void deleteComment(String photoId, String commentId) {
        commentsRef.child(photoId).child(commentId).removeValue();
    }
}
