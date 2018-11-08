package com.developerdru.vividity.screens.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.developerdru.vividity.R;

public class AddPhotoScreen extends AppCompatActivity {

    private static final String KEY_PHOTOID = "photoId";
    private static final String KEY_IS_MY_PIC = "isMyPic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // TODO: Add share functionality using Intent service here
            Snackbar.make(view, "Sharing the image",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        });
    }

    public static Intent getLaunchIntent(@NonNull Context context, @NonNull String photoId,
                                         boolean isMyPic) {
        Intent launchIntent = new Intent(context, AddPhotoScreen.class);
        launchIntent.putExtra(KEY_PHOTOID, photoId);
        launchIntent.putExtra(KEY_IS_MY_PIC, isMyPic);
        return launchIntent;
    }
}
