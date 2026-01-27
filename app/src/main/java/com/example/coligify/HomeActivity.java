package com.example.coligify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.coligify.Fragment.CollegeFinderFragment;
import com.example.coligify.Fragment.ContentFragment;
import com.example.coligify.Fragment.HomeFragment;
import com.example.coligify.Fragment.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton fabAI;

    Fragment homeFragment;
    Fragment collegeFragment;
    Fragment contentFragment;
    Fragment notificationFragment;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // SharedPreferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // 🔐 Login check
        boolean isLogin = preferences.getBoolean("islogin", false);
        if (!isLogin) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 🎉 First-time welcome dialog
        boolean isFirstTime = preferences.getBoolean("isfirsttime", true);
        if (isFirstTime) {
            showWelcomeDialog();
        }

        // Views
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAI = findViewById(R.id.fab_ai);

        bottomNavigationView.setOnItemSelectedListener(this);


        fabAI.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AI_ChatActivity.class))
        );


        homeFragment = new HomeFragment();
        collegeFragment = new CollegeFinderFragment();
        contentFragment = new ContentFragment();
        notificationFragment = new NotificationFragment();


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home, homeFragment)
                    .commit();
        }
    }


    private void showWelcomeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_welcome, null);
        builder.setView(view);

      
        ImageView logo = view.findViewById(R.id.ivimglogo);

        // 🔍 Make logo look BIG
        logo.setScaleX(2.0f);
        logo.setScaleY(2.0f);

        AlertDialog dialog = builder.create();

        // 🔥 Remove default dialog background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.setCancelable(false);

        view.findViewById(R.id.btnWelcome).setOnClickListener(v -> {
            dialog.dismiss();
            editor.putBoolean("isfirsttime", false).apply();
        });

        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment selectedFragment = null;

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            selectedFragment = homeFragment;
        } else if (id == R.id.nav_college) {
            selectedFragment = collegeFragment;
        } else if (id == R.id.nav_ai) {
            selectedFragment = contentFragment;
        } else if (id == R.id.nav_content) {
            selectedFragment = notificationFragment;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home, selectedFragment)
                    .commit();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        finishAffinity(); // Exit app completely
    }
}
