package com.developerdru.vividity.screens.profile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developerdru.vividity.R;
import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.data.remote.OperationStatus;
import com.developerdru.vividity.utils.GlideApp;
import com.developerdru.vividity.utils.Utility;

public class ProfileScreen extends AppCompatActivity implements ProfileUserAdapter.InteractionListener {

    private static final String KEY_FOLLOWS = "follows";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_IS_MY_PROFILE = "isMyProfile";

    private Menu menu;

    private String userId;
    private boolean follows = false;
    private boolean isMyProfile = false;

    // UI Elements
    ImageView imgProfilePic;
    TextView tvProfileName, tvUserIdentifier, tvUserFollowsHeader;
    RecyclerView rvFollowsList;
    View viewOverlay;
    ProgressBar progressIndicator;

    ProfileUserAdapter userAdapter;

    ProfileVM profileVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(KEY_USER_ID) || !extras.containsKey
                (KEY_FOLLOWS) || !extras.containsKey(KEY_IS_MY_PROFILE)) {
            throw new RuntimeException("Missing required args from profile screen");
        }

        follows = extras.getBoolean(KEY_FOLLOWS);
        userId = extras.getString(KEY_USER_ID);
        isMyProfile = extras.getBoolean(KEY_IS_MY_PROFILE);

        initializeUI();

        changeMenuTitle(follows);

        ProfileVMFactory profileVMFactory = new ProfileVMFactory(userId);
        profileVM = ViewModelProviders.of(this, profileVMFactory).get(ProfileVM.class);
        showLoading();

        profileVM.getUserData().observe(this, user -> {
            if (user != null) {
                GlideApp.with(this).load(user.getProfilePicURL()).into(imgProfilePic);
                tvProfileName.setText(user.getDisplayName());
                int providerDrawableRes = Utility.getSignInServiceDrawable(user.getSignInService());
                tvUserIdentifier.setCompoundDrawablesRelativeWithIntrinsicBounds
                        (providerDrawableRes, 0, 0, 0);
                tvUserIdentifier.setText(user.getSignInServiceIdentifier());
                tvUserFollowsHeader.setText(String.format(getString(R.string.user_follows_text),
                        user.getDisplayName()));
                hideLoading();
            } else {
                Toast.makeText(this, R.string.err_user_not_found, Toast.LENGTH_SHORT).show();
                hideLoading();
                finish();
            }
        });

        profileVM.getMyFollows().observe(this, followUsers -> {
            if (followUsers != null) {
                userAdapter.reset(followUsers);
            }
        });
    }

    private void initializeUI() {
        imgProfilePic = findViewById(R.id.imgProfilePic);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvUserIdentifier = findViewById(R.id.tvProfileUserIdentifier);
        tvUserFollowsHeader = findViewById(R.id.tvProfileUserFollows);
        rvFollowsList = findViewById(R.id.rvFollowsList);
        progressIndicator = findViewById(R.id.prg_profile_screen);
        viewOverlay = findViewById(R.id.viewOverlayProfile);

        rvFollowsList.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new ProfileUserAdapter(isMyProfile, this);

        rvFollowsList.setAdapter(userAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (!isMyProfile) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu_profile, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_follow:
                if (!isMyProfile) {
                    showLoading();
                    LiveData<OperationStatus> statusData = profileVM.unfollowUser(userId);
                    statusData.observe(this, status -> {
                        if (status != null && status.isComplete()) {
                            statusData.removeObservers(ProfileScreen.this);
                            hideLoading();
                            follows = !follows;
                            changeMenuTitle(follows);
                        }
                    });
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeMenuTitle(boolean follow) {
        follows = follow;
        if (!isMyProfile) {
            if (follow) {
                menu.findItem(R.id.menu_follow).setTitle(R.string.menu_title_unfollow);
            } else {
                menu.findItem(R.id.menu_follow).setTitle(R.string.menu_title_follow);
            }
        }
    }

    public static Intent getLaunchIntent(@NonNull Context context, @NonNull String userId, boolean
            isMyProfile, boolean follows) {
        Intent launchIntent = new Intent(context, ProfileScreen.class);
        launchIntent.putExtra(KEY_USER_ID, userId);
        launchIntent.putExtra(KEY_FOLLOWS, follows);
        launchIntent.putExtra(KEY_IS_MY_PROFILE, isMyProfile);
        return launchIntent;
    }

    private void showLoading() {
        viewOverlay.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        viewOverlay.setVisibility(View.GONE);
        progressIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onNotificationStatusChange(FollowUser followUser, boolean getNotified) {
        showLoading();
        LiveData<OperationStatus> statusData = profileVM.changeUserNotificationStatus(userId,
                getNotified);
        statusData.observe(this, status -> {
            if (status != null && status.isComplete()) {
                statusData.removeObservers(ProfileScreen.this);
                hideLoading();
            }
        });
    }

    @Override
    public void onDeleteTapped(FollowUser followUser) {
        showLoading();
        LiveData<OperationStatus> statusData = profileVM.unfollowUser(followUser.getUserId());
        statusData.observe(this, status -> {
            if (status != null && status.isComplete()) {
                statusData.removeObservers(ProfileScreen.this);
                userAdapter.unfollowUser(followUser);
                hideLoading();
            }
        });
    }
}
