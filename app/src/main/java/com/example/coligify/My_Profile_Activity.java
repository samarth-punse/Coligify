package com.example.coligify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coligify.Fragment.HomeFragment;

public class My_Profile_Activity extends AppCompatActivity {

    TextView editprofileImg;
    ImageView profileImg,btnback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_profile);

        editprofileImg = findViewById(R.id.tveditprofile);
        btnback = findViewById(R.id.btnBack);

        editprofileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =
                        new Intent(My_Profile_Activity.this, EditProfileActivity.class);
                startActivity(intent);


            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(My_Profile_Activity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }
}
