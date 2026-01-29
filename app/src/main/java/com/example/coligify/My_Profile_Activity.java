package com.example.coligify;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class My_Profile_Activity extends AppCompatActivity {

    TextView editprofileImg;
    ImageView btnBack;
    LinearLayout llLogout;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_profile);

        // SharedPreferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Views
        editprofileImg = findViewById(R.id.tveditprofile);
        btnBack = findViewById(R.id.btnBack);
        llLogout = findViewById(R.id.llLogout); // must exist in XML

        // Edit Profile
        editprofileImg.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Logout click
        llLogout.setOnClickListener(v -> showLogoutDialog());
    }

    // 🔔 SHOW LOGOUT CONFIRMATION DIALOG (ROUNDED)
    private void showLogoutDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        // ⭐ IMPORTANT FIX FOR CURVED CORNERS ⭐
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnLogout = dialog.findViewById(R.id.btnLogout);

        // Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Logout button
        btnLogout.setOnClickListener(v -> {
            dialog.dismiss();
            logout();
        });

        dialog.show();
    }

    // 🔐 LOGOUT LOGIC
    private void logout() {

        // Clear session
        editor.clear();
        editor.apply();

        // Redirect to Login and clear back stack
        Intent intent = new Intent(My_Profile_Activity.this, LoginActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
