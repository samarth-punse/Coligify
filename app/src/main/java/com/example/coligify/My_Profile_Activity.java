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

    LinearLayout llLogout, llFavourites, llDownloads,
            llLanguage, llPermissions, llHelp, llAbout;

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
        llLogout = findViewById(R.id.llLogout);

        llFavourites = findViewById(R.id.llFavourites);
        llDownloads  = findViewById(R.id.llDownloads);
        llLanguage   = findViewById(R.id.llLanguage);
        llPermissions= findViewById(R.id.llPermissions);
        llHelp       = findViewById(R.id.llHelp);
        llAbout      = findViewById(R.id.llAbout);

        // Edit Profile
        editprofileImg.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Menu clicks
        llFavourites.setOnClickListener(v ->
                startActivity(new Intent(this, CombineActivity.class))
        );

        llDownloads.setOnClickListener(v ->
                startActivity(new Intent(this, DownloadsActivity.class))
        );

        llLanguage.setOnClickListener(v ->
                startActivity(new Intent(this, LanguageActivity.class))
        );

        llPermissions.setOnClickListener(v ->
                startActivity(new Intent(this, PermissionsActivity.class))
        );

        llHelp.setOnClickListener(v ->
                startActivity(new Intent(this, HelpFeedbackActivity.class))
        );

        llAbout.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class))
        );

        // Logout click
        llLogout.setOnClickListener(v -> showLogoutDialog());
    }

    // 🔔 SHOW LOGOUT CONFIRMATION DIALOG
    private void showLogoutDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnLogout = dialog.findViewById(R.id.btnLogout);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnLogout.setOnClickListener(v -> {
            dialog.dismiss();
            logout();
        });

        dialog.show();
    }

    // 🔐 LOGOUT LOGIC
    private void logout() {

        editor.clear();
        editor.apply();

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
