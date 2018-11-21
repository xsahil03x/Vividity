package com.developerdru.vividity.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.developerdru.vividity.data.CommentRepository;
import com.developerdru.vividity.data.entities.PhotoComment;
import com.developerdru.vividity.data.entities.User;
import com.developerdru.vividity.utils.FirebasePaths;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommentRepositoryImpl implements CommentRepository {

    private static final String TAG = "PhotoRepositoryImpl";

    private static CommentRepositoryImpl INSTANCE;

    public synchronized static CommentRepositoryImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CommentRepositoryImpl();
        }
        return INSTANCE;
    }

    private DatabaseReference commentsRef;
    private DatabaseReference usersRef;


    private CommentRepositoryImpl() {
        DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
        commentsRef = baseRef.child(FirebasePaths.COMMENTS_DB_PATH);
        usersRef = baseRef.child(FirebasePaths.USERS_DB_PATH);
    }

    @Override
    public LiveData<List<PhotoComment>> getCommentsForPhoto(String photoId) {
        FirebaseQueryLiveData commentsData = new FirebaseQueryLiveData(commentsRef.child(photoId));
        return Transformations.map(commentsData, input -> {
            GenericTypeIndicator<Map<String, PhotoComment>> typeIndicator = new
                    GenericTypeIndicator<Map<String, PhotoComment>>() {
                    };
            Map<String, PhotoComment> photoCommentMap = input.getValue(typeIndicator);
            if (photoCommentMap != null) {
                for (Map.Entry<String, PhotoComment> item : photoCommentMap.entrySet()) {
                    item.getValue().setCommentIdentifier(item.getKey());
                }
            }
            List<PhotoComment> comments = null;
            if (photoCommentMap != null) {
                comments = new ArrayList<>(photoCommentMap.values());
                Collections.sort(comments, (o1, o2) ->
                        (int) (o2.getTimestamp() - o1.getTimestamp()));
            }
            return comments;
        });
    }

    @Override
    public void deleteComment(String photoId, String commentId) {
        commentsRef.child(photoId).child(commentId).removeValue();
    }

    @Override
    public LiveData<OperationStatus> addComment(String photoId, String text, String myId) {
        final MutableLiveData<OperationStatus> status = new MutableLiveData<>();
        usersRef.child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    String commenterName = currentUser.getDisplayName();
                    String commenterPic = currentUser.getProfilePicURL();
                    String key = commentsRef.child(photoId).push().getKey();

                    PhotoComment commentObj = new PhotoComment();
                    commentObj.setCommentIdentifier(key);
                    commentObj.setArtifactId(photoId);
                    commentObj.setCommenterId(myId);
                    commentObj.setCommenterName(commenterName);
                    commentObj.setCommenterPic(commenterPic);
                    commentObj.setText(text);
                    commentObj.setTimestamp(System.currentTimeMillis());

                    assert key != null;
                    commentsRef.child(photoId).child(key).setValue(commentObj)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    status.postValue(OperationStatus.getCompletedStatus());
                                } else {
                                    status.postValue(OperationStatus.getErrorStatus(task
                                            .getException() == null ? "Error" : task
                                            .getException().getMessage()));
                                }
                            });
                } else {
                    status.postValue(OperationStatus.getErrorStatus("User is null"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                status.postValue(OperationStatus.getErrorStatus(databaseError.getMessage()));
            }
        });

        return status;
    }
}
