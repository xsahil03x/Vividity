package com.developerdru.vividity.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.StorageRepository;
import com.developerdru.vividity.utils.FirebasePaths;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class StorageRepositoryImpl implements StorageRepository {

    private static final String TAG = "StorageRepositoryImpl";

    private static StorageRepositoryImpl INSTANCE;

    public synchronized static StorageRepositoryImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StorageRepositoryImpl();
        }
        return INSTANCE;
    }

    private StorageReference storageRef;


    private StorageRepositoryImpl() {
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public LiveData<OperationStatus> uploadProfilePic(Uri localProfilePicUri) {
        // TODO not yet implemented and will probably not be implemented
        return null;
    }

    @Override
    public LiveData<OperationStatus> uploadPhoto(@NonNull Uri localPhotoUri, @NonNull String
            fileName) {

        StorageReference newRef = storageRef.child(FirebasePaths.STORAGE_PHOTOS_PATH).child
                (fileName);

        final MutableLiveData<OperationStatus> status = new MutableLiveData<>();

        newRef.putFile(localPhotoUri)
                .addOnProgressListener(snapshot -> {
                    int progressPct = (int) ((100 * snapshot.getBytesTransferred()) / snapshot
                            .getTotalByteCount());
                    status.postValue(OperationStatus.getInProgresStatus(progressPct));
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return newRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        status.postValue(OperationStatus.getCompletedStatus(task.getResult()
                                .toString()));
                    } else {
                        status.postValue(
                                OperationStatus.getErrorStatus(task.getException() == null
                                        ? "ERROR"
                                        : task.getException().toString()));
                    }
                })
                .addOnFailureListener(e -> {
                    status.postValue(OperationStatus.getErrorStatus(e.getMessage()));
                    e.printStackTrace();
                });
        return status;
    }

    @Override
    public LiveData<OperationStatus> downloadPhoto(@NonNull String storagePath, @NonNull Uri
            destinationUri) {
        StorageReference newRef = FirebaseStorage.getInstance().getReference(storagePath);
        final MutableLiveData<OperationStatus> status = new MutableLiveData<>();

        newRef.getFile(destinationUri)
                .addOnProgressListener(snapshot -> {
                    int progressPct = (int) ((100 * snapshot.getBytesTransferred()) / snapshot
                            .getTotalByteCount());
                    status.postValue(OperationStatus.getInProgresStatus(progressPct));
                })
                .addOnFailureListener(e -> {
                    status.postValue(OperationStatus.getErrorStatus(e.getMessage()));
                    e.printStackTrace();
                })
                .addOnSuccessListener(snapshot -> status.postValue(OperationStatus
                        .getCompletedStatus(destinationUri.toString())));
        return status;
    }
}
