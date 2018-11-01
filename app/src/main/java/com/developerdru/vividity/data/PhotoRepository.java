package com.developerdru.vividity.data;

import android.arch.lifecycle.LiveData;

import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.data.entities.PhotoComment;

import java.util.List;

/**
 * Defines signature for various types of data sources
 */
public interface PhotoRepository {
    LiveData<Photo> getPhotos();

    LiveData<List<PhotoComment>> getCommentsForPhoto(String photoId);
}
