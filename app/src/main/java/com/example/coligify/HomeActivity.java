package com.example.coligify;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.coligify.Fragment.CollegeFinderFragment;
import com.example.coligify.Fragment.ContentFragment;
import com.example.coligify.Fragment.HomeFragment;
import com.example.coligify.Fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton actionButton;

    Fragment homeFragment;
    Fragment collegeFinderFragment;
    Fragment contentFragment;
    Fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        actionButton = findViewById(R.id.fab_ai);

        // ✅ FIXED CONTEXT HERE
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AI_ChatActivity.class);
                startActivity(intent);

            }
        });

        // Initialize fragments
        homeFragment = new HomeFragment();
        collegeFinderFragment = new CollegeFinderFragment();
        contentFragment = new ContentFragment();
        profileFragment = new ProfileFragment();

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home, homeFragment)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home, homeFragment)
                    .commit();

        } else if (id == R.id.nav_college) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home, collegeFinderFragment)
                    .commit();

        } else if (id == R.id.nav_content) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home, profileFragment)
                    .commit();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // exit app cleanly
    }
}