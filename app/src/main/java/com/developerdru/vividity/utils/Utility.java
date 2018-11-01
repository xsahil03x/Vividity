package com.developerdru.vividity.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Utility {

    public static void emailFeedbackIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"example.yahoo.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Vividity feedback - " + System.currentTimeMillis());
        context.startActivity(intent);
    }

}
