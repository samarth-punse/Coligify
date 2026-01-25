package com.example.coligify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    ImageView backbutton, camera;
    CircleImageView ProfileImage;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            ProfileImage.setImageURI(uri); // ✅ correct view
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        backbutton = findViewById(R.id.ivBack);
        ProfileImage = findViewById(R.id.civprofile);
        camera = findViewById(R.id.ivcamera);

        backbutton.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, My_Profile_Activity.class));
            finish();
        });

        // Camera icon click → open gallery
        camera.setOnClickListener(v -> openGallery());

        // Optional: clicking profile image also opens gallery
        ProfileImage.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        imagePickerLauncher.launch("image/*");
    }
}
