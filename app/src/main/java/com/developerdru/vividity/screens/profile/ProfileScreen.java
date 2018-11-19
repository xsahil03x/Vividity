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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.developerdru.vividity.R;
import com.developerdru.vividity.data.entities.FollowUser;
import com.developerdru.vividity.data.entities.User;
import com.developerdru.vividity.data.remote.OperationStatus;
import com.developerdru.vividity.utils.GlideApp;
import com.developerdru.vividity.utils.Utility;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileScreen extends AppCompatActivity implements ProfileUserAdapter
        .InteractionListener {

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
    User user;
    String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            extras = savedInstanceState;
        }
        if (extras == null || !extras.containsKey(KEY_USER_ID) || !extras.containsKey
                (KEY_FOLLOWS) || !extras.containsKey(KEY_IS_MY_PROFILE)) {
            Toast.makeText(this, getString(R.string.missing_required_params), Toast
                    .LENGTH_SHORT).show();
            Crashlytics.log(this.getClass().getSimpleName() + ": params missing");
            finish();
            return;
        }

        if (myId == null || myId.isEmpty()) {
            Toast.makeText(this, getString(R.string.missing_required_params), Toast
                    .LENGTH_SHORT).show();
            Crashlytics.log(this.getClass().getSimpleName() + ": params missing. myId");
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        follows = extras.getBoolean(KEY_FOLLOWS);
        userId = extras.getString(KEY_USER_ID);
        isMyProfile = extras.getBoolean(KEY_IS_MY_PROFILE);

        if (isMyProfile) {
            toolbar.setTitle(R.string.txt_my_profile_screen_header);
        }

        initializeUI();

        ProfileVMFactory profileVMFactory = new ProfileVMFactory(userId, myId);
        profileVM = ViewModelProviders.of(this, profileVMFactory).get(ProfileVM.class);
        showLoading();

        profileVM.getUserData().observe(this, user -> {
            if (user != null) {
                this.user = user;
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
        rvFollowsList = findViewById(R.id.rvUserFollows);
        progressIndicator = findViewById(R.id.prg_profile_screen);
        viewOverlay = findViewById(R.id.viewOverlayProfile);

        rvFollowsList.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new ProfileUserAdapter(isMyProfile, this);

        rvFollowsList.setAdapter(userAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_USER_ID, userId);
        outState.putBoolean(KEY_FOLLOWS, follows);
        outState.putBoolean(KEY_IS_MY_PROFILE, isMyProfile);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_profile, menu);
        changeMenuTitle(follows);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_follow:
                showLoading();
                if (follows) {
                    LiveData<OperationStatus> statusData = profileVM.unfollowUser(userId);
                    statusData.observe(this, status -> {
                        if (status != null && status.isComplete()) {
                            statusData.removeObservers(ProfileScreen.this);
                            hideLoading();
                            follows = !follows;
                            changeMenuTitle(follows);
                        }
                    });
                } else {
                    LiveData<OperationStatus> statusData = profileVM.followUser(userId, user
                            .getDisplayName(), user.getProfilePicURL());
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
            case android.R.id.home:
                ProfileScreen.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeMenuTitle(boolean follows) {
        if (follows) {
            menu.findItem(R.id.menu_follow).setTitle(R.string.menu_title_unfollow);
        } else {
            menu.findItem(R.id.menu_follow).setTitle(R.string.menu_title_follow);
        }
        menu.findItem(R.id.menu_follow).setVisible(!isMyProfile).setEnabled(!isMyProfile);
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
        LiveData<OperationStatus> statusData = profileVM.changeUserNotificationStatus(followUser
                .getUserId(), getNotified);
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

    @Override
    public void onProfilePicTapped(FollowUser user) {

        LiveData<Boolean> followLiveData = profileVM.amIFollowing(user.getUserId());
        followLiveData.observe(this, follows -> {
            followLiveData.removeObservers(this);
            boolean followStatus = follows == null ? false : follows;

            Intent profileIntent = getLaunchIntent(ProfileScreen.this, user.getUserId(),
                    user.getUserId().equalsIgnoreCase(myId), followStatus);
            startActivity(profileIntent);
        });
    }

    public static Intent getLaunchIntent(@NonNull Context context, @NonNull String userId, boolean
            isMyProfile, boolean follows) {
        Intent launchIntent = new Intent(context, ProfileScreen.class);
        launchIntent.putExtra(KEY_USER_ID, userId);
        launchIntent.putExtra(KEY_FOLLOWS, follows);
        launchIntent.putExtra(KEY_IS_MY_PROFILE, isMyProfile);
        return launchIntent;
    }
}
