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

import java.util.ArrayList;
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
        return Transformations.map(commentsData, new Function<DataSnapshot, List<PhotoComment>>() {
            @Override
            public List<PhotoComment> apply(DataSnapshot input) {
                return new ArrayList<>(((Map<String, PhotoComment>) input.getValue())
                        .values());
            }
        });
    }
}
