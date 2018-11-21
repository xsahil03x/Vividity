package com.developerdru.vividity.screens.add;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.PhotoRepository;
import com.developerdru.vividity.data.StorageRepository;
import com.developerdru.vividity.data.remote.OperationStatus;

public class AddPhotoVM extends ViewModel {

    private StorageRepository storageRepository;
    private PhotoRepository photoRepository;

    public AddPhotoVM(@NonNull StorageRepository storageRepository, @NonNull PhotoRepository
            photoRepository) {
        this.storageRepository = storageRepository;
        this.photoRepository = photoRepository;
    }

    public LiveData<OperationStatus> uploadPhoto(@NonNull Uri localFileUri, @NonNull String myId,
                                                 @NonNull String caption) {
        MediatorLiveData<OperationStatus> mediatorLiveData = new MediatorLiveData<>();

        String fileName = myId + "_" + System.currentTimeMillis() + ".jpg";
        LiveData<OperationStatus> uploadLiveData = storageRepository.uploadPhoto(localFileUri,
                fileName);

        mediatorLiveData.addSource(uploadLiveData, uploadStatus -> {
            if (uploadStatus != null) {
                if (uploadStatus.isComplete()) {
                    String downloadUrl = uploadStatus.getExtra();

                    mediatorLiveData.addSource(uploadPhotoData(fileName, myId, caption,
                            downloadUrl), mediatorLiveData::postValue);

                } else {
                    mediatorLiveData.postValue(uploadStatus);
                    if (uploadStatus.isErroneous()) {
                        mediatorLiveData.removeSource(uploadLiveData);
                    }
                }
            }
        });

        return mediatorLiveData;
    }

    private LiveData<OperationStatus> uploadPhotoData(@NonNull String picName, @NonNull String
            myId, @NonNull String caption, @NonNull String downloadUrl) {
        return photoRepository.addPhotoData(picName, myId, caption, downloadUrl);
    }
}
