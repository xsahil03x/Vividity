package com.developerdru.vividity.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.utils.FirebasePaths;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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


    private PhotoRepositoryImpl() {
        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        photosRef = baseRef.child(FirebasePaths.PHOTOS_DB_PATH);
    }

    @Override
    public LiveData<Photo> getPhotos(int orderBy, long startAt, int limit) {
        Query photoQuery = photosRef;
        switch (orderBy) {
            case ORDER_TIME_DESC:
                photoQuery = photoQuery.orderByChild(FirebasePaths.PHOTOS_TIMESTAMP_PATH);
                break;
            case ORDER_UPVOTE_DESC:
                photoQuery = photoQuery.orderByChild(FirebasePaths.PHOTOS_UPVOTE_COUNT_PATH);
                break;
            case ORDER_COMMENT_COUNT_DESC:
                photoQuery = photoQuery.orderByChild(FirebasePaths.PHOTOS_COMMENT_COUNT_PATH);
                break;
        }
        photoQuery = photoQuery.startAt(startAt).limitToFirst(limit);
        FirebaseQueryLiveData photosData = new FirebaseQueryLiveData(photoQuery);
        return Transformations.map(photosData, input -> input.getValue(Photo.class));
    }
}
