package com.developerdru.vividity.screens.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.developerdru.vividity.R;
import com.developerdru.vividity.screens.login.LoginScreen;
import com.developerdru.vividity.utils.Utility;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {

    PhotoAdapter photoAdapter;

    HomeVM homeVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        RecyclerView rvPhotos = findViewById(R.id.rv_photo_list);
        rvPhotos.setLayoutManager(new LinearLayoutManager(this));
        photoAdapter = new PhotoAdapter();
        rvPhotos.setAdapter(photoAdapter);

        HomeVMFactory homeVMFactory = new HomeVMFactory(HomeVM.ORDER_TIME);
        homeVM = ViewModelProviders.of(this, homeVMFactory).get(HomeVM.class);
        observeChanges();
    }

    private void observeChanges() {
        homeVM.getPhotos().observe(this, photos -> photoAdapter.resetPhotos(photos));
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
                // TODO launch my profile page
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
}
