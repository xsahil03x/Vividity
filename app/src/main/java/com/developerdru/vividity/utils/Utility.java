package com.developerdru.vividity.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.developerdru.vividity.R;

import java.io.File;

public class Utility {

    private static final String PROVIDER_GOOGLE = "google.com";
    private static final String PROVIDER_TWITTER = "twitter.com";

    public static void emailFeedbackIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kaushal.devil009@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Vividity feedback - " + System.currentTimeMillis());
        context.startActivity(intent);
    }

    @DrawableRes
    public static int getSignInServiceDrawable(String signInService) {
        switch (signInService) {
            case PROVIDER_GOOGLE:
                return R.drawable.ic_google;
            case PROVIDER_TWITTER:
                return R.drawable.ic_twitter;
            default:
                return R.drawable.ic_firebase;
        }
    }

    public static Uri getFileProviderUri(@NonNull Context appContext, @NonNull File localFile) {
        appContext = appContext.getApplicationContext();
        String provider = appContext.getPackageName() + ".provider";
        return FileProvider.getUriForFile(appContext, provider, localFile);
    }

}
