package com.developerdru.vividity.screens.login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.developerdru.vividity.R;
import com.developerdru.vividity.notification.NotificationUtils;
import com.developerdru.vividity.screens.home.HomeScreen;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.List;

public class LoginScreen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9009;

    private String fcmToken = null;

    private LoginVM loginVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Check if the user has already logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            navigateToHome();
        } else {
            LoginVMFactory loginVMFactory = new LoginVMFactory();
            loginVM = ViewModelProviders.of(this, loginVMFactory).get(LoginVM.class);
            launchLogin();
            getFCMToken();
            // Register notification channel
            NotificationUtils.registerNotificationChannels(this);
        }
    }

    private void navigateToHome() {
        Intent homeScreenIntent = new Intent(this, HomeScreen.class);
        startActivity(homeScreenIntent);
        finish();
    }

    private void launchLogin() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.vividity_logo)
                        .build(),
                RC_SIGN_IN);
    }

    private void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    if (task.getResult() == null) {
                        return;
                    }
                    fcmToken = task.getResult().getToken();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (resultCode == RESULT_OK && user != null) {

                String provider = response == null ? "Firebase" : response.getProviderType();
                String providerId = user.getEmail();
                String userId = user.getUid();
                String pic = user.getPhotoUrl() == null ? null : user.getPhotoUrl()
                        .toString();
                String displayName = user.getDisplayName();
                String profilePic;
                switch (provider) {
                    case "twitter.com":
                        profilePic = pic != null ? pic.replace("normal", "400x400") : null;
                        break;
                    case "google.com":
                        profilePic = pic != null ? pic.replace("s96-c", "s300-c") : null;
                        break;
                    default:
                        profilePic = pic;
                }

                loginVM.updateCurrentUserInfo(userId, provider, profilePic, providerId,
                        displayName, fcmToken)
                        .observe(this, status -> {
                            if (status != null) {
                                if (status.isErroneous()) {
                                    Toast.makeText(this, status.getExtra(), Toast.LENGTH_SHORT)
                                            .show();
                                } else if (status.isComplete()) {
                                    navigateToHome();
                                }
                            }
                        });

            } else {
                Toast.makeText(this, getString(R.string.msg_login_failed_retry), Toast
                        .LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
