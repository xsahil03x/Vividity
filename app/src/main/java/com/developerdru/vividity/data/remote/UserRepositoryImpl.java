package com.developerdru.vividity.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.developerdru.vividity.data.UserRepository;
import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.data.entities.User;
import com.developerdru.vividity.utils.FirebasePaths;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserRepositoryImpl implements UserRepository {

    private static final String TAG = "UserRepositoryImpl";

    private static UserRepositoryImpl INSTANCE;

    public synchronized static UserRepositoryImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRepositoryImpl();
        }
        return INSTANCE;
    }

    private DatabaseReference usersRef;


    private UserRepositoryImpl() {
        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        usersRef = baseRef.child(FirebasePaths.USERS_DB_PATH);
    }

    @Override
    public LiveData<User> getUserInfo(String userId) {
        FirebaseQueryLiveData userData = new FirebaseQueryLiveData(usersRef.child(userId));
        return Transformations.map(userData, input -> input.getValue(User.class));
    }

    @Override
    public LiveData<List<FollowUser>> getFollowerList(String userId) {
        FirebaseQueryLiveData userData = new FirebaseQueryLiveData(usersRef.child(userId)
                .child(FirebasePaths.USER_FOllOWERS_PATH));
        return Transformations.map(userData, input -> {
            List<FollowUser> followers = new ArrayList<>();
            for (DataSnapshot snapshot : input.getChildren()) {
                followers.add(snapshot.getValue(FollowUser.class));
            }
            return followers;
        });
    }

    @Override
    public LiveData<List<FollowUser>> getFollowsList(String userId) {
        FirebaseQueryLiveData userData = new FirebaseQueryLiveData(usersRef.child(userId)
                .child(FirebasePaths.USER_FOllOWS_PATH));
        return Transformations.map(userData, input -> {
            List<FollowUser> follows = new ArrayList<>();
            for (DataSnapshot snapshot : input.getChildren()) {
                follows.add(snapshot.getValue(FollowUser.class));
            }
            return follows;
        });
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
            getNotification) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final MutableLiveData<OperationStatus> status = new MutableLiveData<>();
        if (currentUser != null) {
            usersRef.child(currentUser.getUid())
                    .child(FirebasePaths.USER_FOllOWS_PATH)
                    .child(userId)
                    .child(FirebasePaths.USER_NOTIFICATION_PATH)
                    .setValue(getNotification)
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
    public LiveData<OperationStatus> updateMyInfo(String userId, String provider, String
            profilePic, String providerIdentifier, String displayName, String fcmToken) {

        final MutableLiveData<OperationStatus> status = new MutableLiveData<>();

        Map<String, Object> newValues = new HashMap<>();
        newValues.put(FirebasePaths.USER_PROVIDER_PATH, provider);
        newValues.put(FirebasePaths.USER_PROVIDER_IDENTIFIER_PATH, providerIdentifier);
        newValues.put(FirebasePaths.USER_ID_PATH, userId);
        newValues.put(FirebasePaths.USER_PIC_PATH, profilePic);
        newValues.put(FirebasePaths.USER_NAME_PATH, displayName);
        newValues.put(FirebasePaths.USER_FCM_TOKEN_PATH, fcmToken);

        usersRef.child(userId).updateChildren(newValues)
                .addOnSuccessListener(a -> status.postValue(OperationStatus
                        .getCompletedStatus()))
                .addOnFailureListener(ex -> {
                    status.postValue(OperationStatus.getErrorStatus(ex.getMessage()));
                    ex.printStackTrace();
                });


        return status;
    }

    @Override
    public LiveData<OperationStatus> updateMyFCMToken(@NonNull String fcmToken) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final MutableLiveData<OperationStatus> status = new MutableLiveData<>();
        if (currentUser != null) {
            usersRef.child(currentUser.getUid())
                    .child(FirebasePaths.USER_FCM_TOKEN_PATH)
                    .setValue(fcmToken)
                    .addOnSuccessListener(aVoid -> status.postValue(OperationStatus
                            .getCompletedStatus()))
                    .addOnFailureListener(e -> {
                        status.postValue(OperationStatus.getErrorStatus(e.getMessage()));
                        e.printStackTrace();
                    });
        }
        return status;
    }
}
