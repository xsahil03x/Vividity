package com.developerdru.vividity.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.utils.FirebasePaths;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public LiveData<List<Photo>> getPhotos(int orderBy) {
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

        FirebaseQueryLiveData photosData = new FirebaseQueryLiveData(photoQuery);

        return Transformations.map(photosData, input -> {
            List<Photo> photos = new ArrayList<>();
            for (DataSnapshot snapshot : input.getChildren()) {
                Photo singlePhoto = snapshot.getValue(Photo.class);
                if (singlePhoto != null) {
                    singlePhoto.setPicIdentifier(snapshot.getKey());
                    photos.add(singlePhoto);
                }
            }
            Collections.reverse(photos);
            return photos;
        });
    }
}
