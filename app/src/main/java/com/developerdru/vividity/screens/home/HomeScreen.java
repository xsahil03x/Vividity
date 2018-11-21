package com.developerdru.vividity.screens.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.developerdru.vividity.R;
import com.developerdru.vividity.data.entities.Photo;
import com.developerdru.vividity.screens.add.AddPhotoScreen;
import com.developerdru.vividity.screens.details.PhotoDetailsScreen;
import com.developerdru.vividity.screens.login.LoginScreen;
import com.developerdru.vividity.screens.profile.ProfileScreen;
import com.developerdru.vividity.utils.Utility;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity implements PhotoAdapter.OnClickListener {

    public static final String KEY_PHOTO_ID = "home_photo_id";

    PhotoAdapter photoAdapter;

    HomeVM homeVM;

    private String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // UI elements
    View progressBar, overlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        overlayView = findViewById(R.id.viewOverlayHome);
        progressBar = findViewById(R.id.prg_home_screen);
        RecyclerView rvPhotos = findViewById(R.id.rv_photo_list);
        rvPhotos.setLayoutManager(new LinearLayoutManager(this));
        photoAdapter = new PhotoAdapter(this);
        rvPhotos.setAdapter(photoAdapter);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_action);
        fabAdd.setOnClickListener(v -> {
            Intent addIntent = new Intent(this, AddPhotoScreen.class);
            startActivity(addIntent);
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String photoId = extras.getString(KEY_PHOTO_ID);
            if (photoId != null) {
                Intent detailIntent = PhotoDetailsScreen.getLaunchIntent(this, photoId);
                startActivity(detailIntent);
                finish();
            }
        }

        HomeVMFactory homeVMFactory = new HomeVMFactory(HomeVM.ORDER_TIME, myId);
        homeVM = ViewModelProviders.of(this, homeVMFactory).get(HomeVM.class);
        observeChanges();
    }

    private void observeChanges() {
        showLoading();
        homeVM.getPhotos().observe(this, photos -> {
            photoAdapter.resetPhotos(photos);
            hideLoading();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                Intent profileIntent = ProfileScreen.getLaunchIntent(this, myId, true, false);
                startActivity(profileIntent);
                return true;
            case R.id.menu_feedback:
                Utility.emailFeedbackIntent(this);
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(this, LoginScreen.class);
                startActivity(loginIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoading() {
        overlayView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        overlayView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPhotoTapped(Photo photo) {
        Intent detailIntent = PhotoDetailsScreen.getLaunchIntent(this, photo.getPicIdentifier());
        startActivity(detailIntent);
    }

    @Override
    public void onUploaderTapped(Photo photo) {
        String uploaderId = photo.getUploaderId();
        LiveData<Boolean> followLiveData = homeVM.amIFollowing(uploaderId);
        followLiveData.observe(this, follows -> {
            followLiveData.removeObservers(this);
            boolean followStatus = follows == null ? false : follows;
            Intent profileScreenIntent = ProfileScreen.getLaunchIntent(HomeScreen.this, uploaderId,
                    uploaderId.equalsIgnoreCase(myId), followStatus);
            startActivity(profileScreenIntent);
        });
    }
}
