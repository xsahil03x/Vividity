package com.developerdru.vividity.screens.details;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.developerdru.vividity.R;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.data.entities.PhotoComment;
import com.developerdru.vividity.data.remote.OperationStatus;
import com.developerdru.vividity.screens.profile.ProfileScreen;
import com.developerdru.vividity.services.Downloader;
import com.developerdru.vividity.utils.GlideApp;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PhotoDetailsScreen extends AppCompatActivity implements CommentAdapter.Listener,
        View.OnClickListener {

    public static final String KEY_PHOTO_ID = "photoId";

    private static final String DATE_FORMAT = "MMM-dd, yyyy";

    private static final int RC_WRITE_EXTERNAL_STORAGE = 22;

    RecyclerView rvComments;
    ImageView imgPhotoDetails, imgUploader, imgSendComment;
    EditText etComment;
    TextView tvUploadDate, tvUpVoteCount, tvCommentCount, tvUploaderName;
    FloatingActionButton fabShare;
    View overlayView, progressBar;

    Toolbar toolbar;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    private String photoId, photoDownloadURL, photoName;
    private CommentAdapter commentAdapter;
    private PhotoDetailsVM photoDetailsVM;
    private boolean isShareIntended = false;

    String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details_screen);
        toolbar = findViewById(R.id.toolbar_img_deails);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

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
            Crashlytics.log(this.getClass().getSimpleName() + ": params missing. photoId: " +
                    photoId);
            finish();
            return;
        }

        PhotoDetailsVMFactory factory = new PhotoDetailsVMFactory(photoId, myId);
        photoDetailsVM = ViewModelProviders.of(this, factory).get(PhotoDetailsVM.class);

        photoDetailsVM.getPhotoMetadata().observe(this, this::populatePhotoDetails);

        photoDetailsVM.getCommentLiveData().observe(this, this::populateComments);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_PHOTO_ID, photoId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (upIntent != null && NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else if (upIntent != null) {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.menu_details_download:
                requireSDCardPermission();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_WRITE_EXTERNAL_STORAGE)
    private void requireSDCardPermission() {
        String perms = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (EasyPermissions.hasPermissions(this, perms)) {
            Downloader.enqueue(this, photoName, photoDownloadURL, isShareIntended);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string
                    .write_external_sd_rationale), RC_WRITE_EXTERNAL_STORAGE, perms);
        }
        if (isShareIntended) {
            isShareIntended = false;
        }
    }

    private void initializeUI() {

        rvComments = findViewById(R.id.rvPhotoComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(myId, this);
        rvComments.setAdapter(commentAdapter);

        fabShare = findViewById(R.id.fabDetailsShare);
        fabShare.setOnClickListener(this);

        imgPhotoDetails = findViewById(R.id.imgDetailsMain);
        imgUploader = findViewById(R.id.imgUploader);
        imgUploader.setOnClickListener(this);
        tvUploadDate = findViewById(R.id.tvUploadDate);
        tvUpVoteCount = findViewById(R.id.tvUpvoteCount);
        tvUpVoteCount.setOnClickListener(this);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        tvUploaderName = findViewById(R.id.tvUploaderName);

        progressBar = findViewById(R.id.prg_details_screen);
        overlayView = findViewById(R.id.viewOverlayDetails);

        imgSendComment = findViewById(R.id.imgSendComment);
        imgSendComment.setOnClickListener(this);
        etComment = findViewById(R.id.etAddComment);
    }

    private void populatePhotoDetails(Photo photo) {

        if (photo == null) {
            return;
        }
        this.userId = photo.getUploaderId();
        this.photoDownloadURL = photo.getDownloadURL();
        this.photoName = photo.getPicName();

        // Populate Image and caption
        GlideApp.with(this).load(photo.getDownloadURL())
                .placeholder(R.drawable.ic_logo_baby)
                .error(R.drawable.ic_baby_mono)
                .into(imgPhotoDetails);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(photo.getCaption());
        }

        // populate image metadata
        tvUploadDate.setText(dateFormatter.format(photo.getTimestamp()));
        tvUpVoteCount.setText(String.valueOf(photo.getUpvoteCount()));
        tvCommentCount.setText(String.valueOf(photo.getCommentsCount()));

        // populate Uploader Info
        GlideApp.with(this).load(photo.getUploaderPic())
                .placeholder(R.drawable.ic_logo_baby)
                .error(R.drawable.ic_baby_mono)
                .into(imgUploader);
        tvUploaderName.setText(photo.getUploader());
    }

    private void populateComments(List<PhotoComment> comments) {
        if (comments == null) {
            return;
        }
        commentAdapter.addComments(comments);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgUploader:
                openProfileScreen(userId);
                break;
            case R.id.tvUpvoteCount:
                showLoading();
                LiveData<OperationStatus> statusLiveData = photoDetailsVM.incrementUpvoteCount();
                statusLiveData.observe(this, status -> {
                    statusLiveData.removeObservers(PhotoDetailsScreen.this);
                    if (status != null && status.isComplete()) {
                        Toast.makeText(this, R.string.msg_upvote_done, Toast.LENGTH_SHORT).show();
                    }
                    hideLoading();
                });
                break;
            case R.id.fabDetailsShare:
                isShareIntended = true;
                requireSDCardPermission();
                break;
            case R.id.imgSendComment:
                String commentText = etComment.getText().toString().trim();
                if (commentText.isEmpty()) {
                    break;
                }
                showLoading();
                LiveData<OperationStatus> statusData = photoDetailsVM.addComment(commentText);
                statusData.observe(PhotoDetailsScreen.this, status -> {
                    statusData.removeObservers(this);
                    if (status != null && status.isComplete()) {
                        Toast.makeText(this, R.string.msg_comment_added, Toast.LENGTH_SHORT).show();
                        etComment.setText("");
                    }
                    hideLoading();
                });
                break;
        }
    }

    @Override
    public void onDeleteTapped(@NonNull PhotoComment comment) {
        photoDetailsVM.deleteComment(comment);
    }

    @Override
    public void onCommenterImageTapped(@NonNull PhotoComment comment) {
        String uploaderId = comment.getCommenterId();
        openProfileScreen(uploaderId);
    }

    private void openProfileScreen(String uploaderId) {
        LiveData<Boolean> followLiveData = photoDetailsVM.amIFollowing(uploaderId);
        followLiveData.observe(this, follows -> {
            followLiveData.removeObservers(this);
            boolean followStatus = follows == null ? false : follows;
            Intent profileScreenIntent = ProfileScreen.getLaunchIntent(PhotoDetailsScreen.this,
                    uploaderId, uploaderId.equalsIgnoreCase(myId), followStatus);
            startActivity(profileScreenIntent);
        });
    }

    void showLoading() {
        overlayView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideLoading() {
        overlayView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    public static Intent getLaunchIntent(@NonNull Context context, @NonNull String photoId) {
        Intent launchIntent = new Intent(context, PhotoDetailsScreen.class);
        launchIntent.putExtra(KEY_PHOTO_ID, photoId);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return launchIntent;
    }
}
