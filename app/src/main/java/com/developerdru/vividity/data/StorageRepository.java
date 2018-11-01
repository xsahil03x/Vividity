package com.developerdru.vividity.data;

import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.remote.OperationStatus;

/**
 * Defines signature for various types of data sources
 */
public interface StorageRepository {

    LiveData<OperationStatus> uploadProfilePic(Uri localProfilePicUri);

    LiveData<OperationStatus> uploadPhoto(@NonNull Uri localPhotoUri, @NonNull String fileName);

    LiveData<OperationStatus> downloadPhoto(@NonNull String storagePath, @NonNull Uri
            localPhotoUri);
}
