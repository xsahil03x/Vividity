package com.developerdru.vividity.screens.add;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.developerdru.vividity.R;
import com.developerdru.vividity.utils.GlideApp;
import com.developerdru.vividity.utils.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AddPhotoScreen extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_PERMISSION_CAMERA = 11;
    private static final int RC_PERMISSION_GALLERY = 13;

    private static final int RC_CAMERA_SELECTION = 19;
    private static final int RC_GALLERY_SELECTION = 93;

    private String cameraFileName;

    private ImageView imgNewPhoto;
    private EditText etAddPicCaption;
    private TextView tvProgressStatus, tvUploadProgress;
    private ProgressBar progressDialog;
    private View overlayView;

    String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Uri imageUri;

    AddPhotoVM addPhotoVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_screen);

        Toolbar toolbar = findViewById(R.id.toolbar_add_photo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        overlayView = findViewById(R.id.viewOverlayAddPhoto);
        progressDialog = findViewById(R.id.prg_addphoto_screen);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        tvUploadProgress = findViewById(R.id.tv_upload_progress);
        imgNewPhoto = findViewById(R.id.imgAddPic);
        FloatingActionButton fabUpload = findViewById(R.id.fab_add_pic_add);
        etAddPicCaption = findViewById(R.id.etAddPicCaption);
        tvProgressStatus = findViewById(R.id.tv_add_pic_status);
        fabUpload.setOnClickListener(this);
        imgNewPhoto.setOnClickListener(this);

        AddPhotoVMFactory factory = new AddPhotoVMFactory();
        addPhotoVM = ViewModelProviders.of(this, factory).get(AddPhotoVM.class);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_pic_add:
                String caption = etAddPicCaption.getText().toString();
                if (caption.isEmpty()) {
                    caption = getString(R.string.default_caption_for_photo);
                }
                if (imageUri != null) {
                    showLoading();
                    addPhotoVM.uploadPhoto(imageUri, myId, caption).observe(this, status -> {
                        if (status != null) {
                            if (status.isErroneous()) {
                                hideLoading();
                                tvProgressStatus.setText(getString(R.string.something_went_wrong));
                            } else if (status.isInProgress()) {
                                updateProgress(status.getCompletionPercentage());
                                progressDialog.setProgress(status.getCompletionPercentage());
                            } else if (status.isComplete()) {
                                hideLoading();
                                tvProgressStatus.setText(R.string.txt_photo_upload_done);
                            }
                        }
                    });
                }
                break;
            case R.id.imgAddPic:
                requestPermissions();
                break;
        }
    }

    private void hideLoading() {
        overlayView.setVisibility(View.GONE);
        progressDialog.setProgress(0);
        progressDialog.setVisibility(View.GONE);
    }

    private void showLoading() {
        overlayView.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.VISIBLE);
    }

    private void updateProgress(int progressPct) {
        String progressText = String.format(Locale.US, getString
                (R.string.txt_download_status), progressPct);
        tvProgressStatus.setText(progressText);
        tvUploadProgress.setText(progressText);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == RC_CAMERA_SELECTION) {
            File imageFile = new File(getExternalFilesDir(Environment
                    .DIRECTORY_PICTURES), cameraFileName + ".jpg");
            Uri fileUri = Utility.getFileProviderUri(this, imageFile);

            // Using crop image library
            CropImage.activity(fileUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(16, 9)
                    .setFixAspectRatio(true)
                    .start(this);
        } else if (resultCode == RESULT_OK && requestCode == RC_GALLERY_SELECTION) {
            if (data != null && data.getData() != null) {
                Uri fileUri = data.getData();

                // Using crop image library
                CropImage.activity(fileUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(16, 9)
                        .setFixAspectRatio(true)
                        .start(this);
            }
        } else if (resultCode == RESULT_OK && requestCode == CropImage
                .CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                imageUri = result.getUri();
                GlideApp.with(AddPhotoScreen.this)
                        .load(imageUri)
                        .into(imgNewPhoto);
            }
        } else if (resultCode == RESULT_OK && requestCode == CropImage
                .CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                Crashlytics.logException(result.getError());
                result.getError().printStackTrace();
            }
        }
    }

    @AfterPermissionGranted(RC_PERMISSION_CAMERA)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            cameraFileName = myId + System.currentTimeMillis();
            Utility.getImageSrcSelectionDialog(AddPhotoScreen.this, cameraFileName,
                    RC_GALLERY_SELECTION, RC_CAMERA_SELECTION).show();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.camera_sdcard_rationale),
                    RC_PERMISSION_CAMERA, perms);
        }
    }
}
