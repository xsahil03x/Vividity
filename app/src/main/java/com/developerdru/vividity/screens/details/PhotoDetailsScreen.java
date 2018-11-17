package com.developerdru.vividity.screens.details;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developerdru.vividity.R;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.data.entities.PhotoComment;
import com.developerdru.vividity.utils.GlideApp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PhotoDetailsScreen extends AppCompatActivity implements CommentAdapter.Listener {

    private static final String KEY_PHOTO_ID = "photoId";

    private static final String DATE_FORMAT = "mmm-dd, yyyy";

    RecyclerView rvComments;
    ImageView imgPhotoDetails, imgUploader;
    TextView tvUploadDate, tvUpVoteCount, tvCommentCount, tvUploaderName;
    FloatingActionButton fabShare;

    Toolbar toolbar;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    private String photoId;
    private CommentAdapter commentAdapter;
    private PhotoDetailsVM photoDetailsVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details_screen);
        toolbar = findViewById(R.id.toolbar_img_deails);
        setSupportActionBar(toolbar);

        initializeUI();

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            photoId = extras.getString(KEY_PHOTO_ID);
        } else if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PHOTO_ID)) {
            photoId = savedInstanceState.getString(KEY_PHOTO_ID);
        }

        if (photoId == null || photoId.isEmpty()) {
            Toast.makeText(this, getString(R.string.missing_required_params), Toast
                    .LENGTH_SHORT).show();
            finish();
            return;
        }

        PhotoDetailsVMFactory factory = new PhotoDetailsVMFactory(photoId);
        photoDetailsVM = ViewModelProviders.of(this, factory).get(PhotoDetailsVM
                .class);

        photoDetailsVM.getPhotoMetadata().observe(this, this::populatePhotoDetails);

        photoDetailsVM.getCommentLiveData().observe(this, this::populateComments);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_PHOTO_ID, photoId);
        super.onSaveInstanceState(outState);
    }

    private void initializeUI() {

        rvComments = findViewById(R.id.rvPhotoComments);
        commentAdapter = new CommentAdapter(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                this);
        rvComments.setAdapter(commentAdapter);

        fabShare = findViewById(R.id.fabDetailsShare);

        imgPhotoDetails = findViewById(R.id.imgDetailsMain);
        imgUploader = findViewById(R.id.imgUploader);
        tvUploadDate = findViewById(R.id.tvUploadDate);
        tvUpVoteCount = findViewById(R.id.tvUpvoteCount);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        tvUploaderName = findViewById(R.id.tvUploaderName);

    }

    private void populatePhotoDetails(Photo photo) {

        if (photo == null) {
            return;
        }

        // Populate Image and caption
        GlideApp.with(this).load(photo.getDownloadURL()).into(imgPhotoDetails);
        toolbar.setTitle(photo.getCaption());

        // populate image metadata
        tvUploadDate.setText(dateFormatter.format(photo.getTimestamp()));
        tvUpVoteCount.setText(String.valueOf(photo.getUpvoteCount()));
        tvCommentCount.setText(String.valueOf(photo.getCommentsCount()));

        // populate Uploader Info
        GlideApp.with(this).load(photo.getUploaderPic()).into(imgUploader);
        tvUploaderName.setText(photo.getUploader());
    }

    private void populateComments(List<PhotoComment> comments) {
        if (comments == null) {
            return;
        }
        commentAdapter.addComments(comments);
    }

    public static Intent getLaunchIntent(@NonNull Context context, @NonNull String photoId) {
        Intent launchIntent = new Intent(context, PhotoDetailsScreen.class);
        launchIntent.putExtra(KEY_PHOTO_ID, photoId);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return launchIntent;
    }

    @Override
    public void onDeleteTapped(@NonNull PhotoComment comment) {
        photoDetailsVM.deleteComment(comment);
    }
}
