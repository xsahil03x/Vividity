package com.developerdru.vividity.data.remote;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.data.entities.PhotoComment;
import com.developerdru.vividity.utils.FirebasePaths;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhotoRepositoryImpl implements PhotoRepository {

    private static final String TAG = "PhotoRepositoryImpl";

    private static PhotoRepositoryImpl INSTANCE;

    public synchronized static PhotoRepositoryImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PhotoRepositoryImpl();
        }
        return INSTANCE;
    }

    private DatabaseReference photosRef;
    private DatabaseReference commentsRef;


    private PhotoRepositoryImpl() {
        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        photosRef = baseRef.child(FirebasePaths.PHOTOS_DB_PATH);
        commentsRef = baseRef.child(FirebasePaths.COMMENTS_DB_PATH);
    }

    @Override
    public LiveData<Photo> getPhotos() {
        FirebaseQueryLiveData photosData = new FirebaseQueryLiveData(photosRef);
        return Transformations.map(photosData, input -> input.getValue(Photo.class));
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
