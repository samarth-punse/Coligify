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
import com.example.coligify.Fragment.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnItemSelectedListener {

    // Views
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAi;

    // Fragments
    private Fragment homeFragment;
    private Fragment collegeFinderFragment;
    private Fragment contentFragment;
    private Fragment notificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAi = findViewById(R.id.fab_ai);

        bottomNavigationView.setOnItemSelectedListener(this);

        // Initialize fragments
        homeFragment = new HomeFragment();
        collegeFinderFragment = new CollegeFinderFragment();
        contentFragment = new ContentFragment();
        notificationFragment = new NotificationFragment();

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home, homeFragment)
                    .commit();
        }

        // Floating Action Button → Open AI Chat Activity
        fabAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AI_ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Fragment selectedFragment = null;

        if (id == R.id.nav_home) {
            selectedFragment = homeFragment;

        } else if (id == R.id.nav_college) {
            selectedFragment = collegeFinderFragment;

        } else if (id == R.id.nav_content) {
            selectedFragment = notificationFragment;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home, selectedFragment)
                    .commit();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        // Exit app completely
        finishAffinity();
    }
}
