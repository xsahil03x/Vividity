package com.developerdru.vividity.data.remote;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.developerdru.vividity.data.DataSource;
import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.data.entities.OperationStatus;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.data.entities.PhotoComment;
import com.developerdru.vividity.data.entities.User;
import com.developerdru.vividity.utils.FirebasePaths;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseDataSource implements DataSource.Photo, DataSource.User, DataSource.Storage {

    private static final String TAG = "DataSource";

    private static FirebaseDataSource INSTANCE;

    public synchronized static FirebaseDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseDataSource();
        }
        return INSTANCE;
    }

    private DatabaseReference photosRef;
    private DatabaseReference commentsRef;
    private DatabaseReference usersRef;

    private StorageReference storageRef;


    private FirebaseDataSource() {
        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        photosRef = baseRef.child(FirebasePaths.PHOTOS_DB_PATH);
        commentsRef = baseRef.child(FirebasePaths.COMMENTS_DB_PATH);
        usersRef = baseRef.child(FirebasePaths.USERS_DB_PATH);
        storageRef = FirebaseStorage.getInstance().getReference();
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

    @Override
    public LiveData<User> getUserInfo(String userId) {
        FirebaseQueryLiveData userData = new FirebaseQueryLiveData(usersRef.child(userId));
        return Transformations.map(userData, input -> input.getValue(User.class));
    }

    @Override
    public LiveData<FollowUser> getFollowerList(String userId) {
        FirebaseQueryLiveData userData = new FirebaseQueryLiveData(usersRef.child(userId)
                .child(FirebasePaths.USER_FOllOWERS_PATH));
        return Transformations.map(userData, input -> input.getValue(FollowUser.class));
    }

    @Override
    public LiveData<FollowUser> getFollowsList(String userId) {
        FirebaseQueryLiveData userData = new FirebaseQueryLiveData(usersRef.child(userId)
                .child(FirebasePaths.USER_FOllOWS_PATH));
        return Transformations.map(userData, input -> input.getValue(FollowUser.class));
    }

    @Override
    public LiveData<OperationStatus> followUser(@NonNull String userId, @NonNull String userName,
                                                @NonNull String profilePic) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final MutableLiveData<OperationStatus> status = new MutableLiveData<>();

        if (currentUser != null) {
            String myId = currentUser.getUid();
            String myProfilePic = currentUser.getPhotoUrl() == null ? "" : currentUser.getPhotoUrl()
                    .toString();
            String myDisplayName = currentUser.getDisplayName() == null ? "Unknown" :
                    currentUser.getDisplayName();

            Map<String, Object> currentUserInfo = new HashMap<>();
            currentUserInfo.put(FirebasePaths.USER_ID_PATH, myId);
            currentUserInfo.put(FirebasePaths.USER_PIC_PATH, myProfilePic);
            currentUserInfo.put(FirebasePaths.USER_NAME_PATH, myDisplayName);

            Map<String, Object> infoOfUserToFollow = new HashMap<>();
            infoOfUserToFollow.put(FirebasePaths.USER_ID_PATH, userId);
            infoOfUserToFollow.put(FirebasePaths.USER_PIC_PATH, profilePic);
            infoOfUserToFollow.put(FirebasePaths.USER_NAME_PATH, userName);
            infoOfUserToFollow.put(FirebasePaths.USER_NOTIFICATION_PATH, false);

            // Add in my followed list
            usersRef.child(myId).child(FirebasePaths.USER_FOllOWS_PATH).child(userId)
                    .setValue(infoOfUserToFollow)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        // Add me in followed users follower list
                        return usersRef.child(userId).child(FirebasePaths.USER_FOllOWERS_PATH)
                                .child(myId)
                                .setValue(currentUserInfo);
                    })
                    .addOnSuccessListener(av -> status.postValue(OperationStatus
                            .getCompletedStatus()))
                    .addOnFailureListener(ex -> {
                        status.postValue(OperationStatus.getErrorStatus(ex.getMessage()));
                        ex.printStackTrace();
                    });
        } else {
            status.postValue(OperationStatus.getErrorStatus("current user is null"));
            Log.e(TAG, "followUser: current user is null");
        }
        return status;
    }

    @Override
    public LiveData<OperationStatus> unFollowUser(@NonNull String userId) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        final MutableLiveData<OperationStatus> status = new MutableLiveData<>();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            // Remove from my followed list
            usersRef.child(currentUserId).child(FirebasePaths.USER_FOllOWS_PATH).child(userId)
                    .removeValue((dbError, dbRef) -> {
                        if (dbError == null) {

                            // Remove from followed users follower list
                            usersRef.child(userId).child(FirebasePaths.USER_FOllOWERS_PATH)
                                    .child(currentUserId)
                                    .removeValue((dbError2, dbRef2) -> {
                                        if (dbError2 == null) {
                                            status.postValue(OperationStatus.getCompletedStatus());
                                        } else {
                                            status.postValue(OperationStatus.getErrorStatus
                                                    (dbError2.getDetails()));
                                            Log.e(TAG, "unFollowUser2: " + dbError2.getDetails());
                                        }
                                    });

                        } else {
                            status.postValue(OperationStatus.getErrorStatus(dbError.getDetails()));
                            Log.e(TAG, "unFollowUser: " + dbError.getDetails());
                        }
                    });
        } else {
            status.postValue(OperationStatus.getErrorStatus("current user is null"));
            Log.e(TAG, "unFollowUser: current user is null");
        }
        return status;
    }

    @Override
    public LiveData<OperationStatus> updateNotificationSetting(@NonNull String userId, boolean
            follow) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final MutableLiveData<OperationStatus> status = new MutableLiveData<>();
        if (currentUser != null) {
            usersRef.child(currentUser.getUid())
                    .child(FirebasePaths.USER_FOllOWS_PATH)
                    .child(userId)
                    .child(FirebasePaths.USER_NOTIFICATION_PATH)
                    .setValue(follow)
                    .addOnSuccessListener(a -> status.postValue(OperationStatus
                            .getCompletedStatus()))
                    .addOnFailureListener(ex -> {
                        status.postValue(OperationStatus.getErrorStatus(ex.getMessage()));
                        ex.printStackTrace();
                    });
        } else {
            status.postValue(OperationStatus.getErrorStatus("current user is null"));
        }
        return status;
    }

    @Override
    public LiveData<OperationStatus> updateMyInfo(@Nullable String displayName, @Nullable String
            profilePicName, @Nullable String profilePicUrl) {
        // TODO not yet implemented and will probably not be implemented
        return null;
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
