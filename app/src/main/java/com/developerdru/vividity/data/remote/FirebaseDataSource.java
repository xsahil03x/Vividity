package com.developerdru.vividity.data.remote;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.developerdru.vividity.data.DataSource;
import com.developerdru.vividity.data.entities.FollowUser;
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

    private DatabaseReference baseRef;
    private DatabaseReference photosRef;
    private DatabaseReference commentsRef;
    private DatabaseReference usersRef;

    private StorageReference storageRef;


    private FirebaseDataSource() {
        baseRef = FirebaseDatabase.getInstance().getReference();
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
    public void followUser(@NonNull String userId, @NonNull String userName, @NonNull String
            profilePic, @NonNull DataSource.User.Listener listener) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
                    .addOnSuccessListener(av -> listener.onComplete())
                    .addOnFailureListener(ex -> {
                        listener.onError();
                        ex.printStackTrace();
                    });
        } else {
            listener.onError();
            Log.e(TAG, "followUser: current user is null");
        }
    }

    @Override
    public void unFollowUser(@NonNull String userId, @NonNull DataSource.User.Listener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                                            listener.onComplete();
                                        } else {
                                            listener.onError();
                                            Log.e(TAG, "unFollowUser2: " + dbError2.getDetails());
                                        }
                                    });

                        } else {
                            listener.onError();
                            Log.e(TAG, "unFollowUser: " + dbError.getDetails());
                        }
                    });
        } else {
            listener.onError();
            Log.e(TAG, "unFollowUser: current user is null");
        }
    }

    @Override
    public void updateNotificationSetting(@NonNull String userId, boolean follow, @NonNull
            DataSource.User.Listener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            usersRef.child(currentUser.getUid())
                    .child(FirebasePaths.USER_FOllOWS_PATH)
                    .child(userId)
                    .child(FirebasePaths.USER_NOTIFICATION_PATH)
                    .setValue(follow)
                    .addOnSuccessListener(a -> listener.onComplete())
                    .addOnFailureListener(ex -> {
                        listener.onError();
                        ex.printStackTrace();
                    });
        } else {
            listener.onError();
        }
    }

    @Override
    public void updateMyInfo(@Nullable String displayName, @Nullable String profilePicName,
                             @Nullable String profilePicUrl, @NonNull DataSource.User.Listener
                                     listener) {
        // TODO not yet implemented and will probably not be implemented

    }

    @Override
    public void uploadProfilePic(Uri localProfilePicUri, DataSource.Storage.Listener listener) {
        // TODO not yet implemented and will probably not be implemented
    }

    @Override
    public void uploadPhoto(@NonNull Uri localPhotoUri, @NonNull String fileName, @NonNull DataSource
            .Storage.Listener listener) {

        StorageReference newRef = storageRef.child(FirebasePaths.STORAGE_PHOTOS_PATH).child
                (fileName);

        newRef.putFile(localPhotoUri)
                .addOnProgressListener(snapshot -> listener.onProgressUpdate(snapshot
                        .getTotalByteCount(), snapshot.getBytesTransferred()))
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    e.printStackTrace();
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return newRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        listener.onComplete(task.getResult().toString(), fileName);
                    } else {
                        listener.onError(task.getException() == null ? "ERROR" : task.getException()
                                .toString());
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onError(e.getMessage());
                    e.printStackTrace();
                });
    }
}
