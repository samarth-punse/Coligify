package com.example.coligify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coligify.Utils.CourseSaveManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔑 STEP 5: INITIALIZE SAVED COURSES (VERY IMPORTANT)
        CourseSaveManager.init(this);

        // ⏳ Splash delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // ➡ Go to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }, 3000);
    }
}
