package com.example.coligify;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coligify.Adapter.CourseAdapter;
import com.example.coligify.Utils.CourseSaveManager;

public class CombineActivity extends AppCompatActivity {

    private RecyclerView rvCourses;
    private Button btnFavourite, btnWatchLater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine);

        btnFavourite = findViewById(R.id.btnFavourite);
        btnWatchLater = findViewById(R.id.btnWatchLater);
        rvCourses = findViewById(R.id.rvCourses);

        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setHasFixedSize(true);

        // 🔥 VERY IMPORTANT – prevent RecyclerView crashes
        rvCourses.setItemAnimator(null);

        // ❤️ Default → Favourite
        loadFavourite();

        // ❤️ Favourite button
        btnFavourite.setOnClickListener(v -> loadFavourite());

        // 🔖 Watch Later button
        btnWatchLater.setOnClickListener(v -> loadWatchLater());
    }

    // ================= LOAD FAVOURITE =================
    private void loadFavourite() {
        CourseAdapter adapter = new CourseAdapter(
                this,
                CourseSaveManager.getFavouriteList(),
                CourseAdapter.MODE_FAVOURITE
        );
        rvCourses.setAdapter(adapter);
    }

    // ================= LOAD WATCH LATER =================
    private void loadWatchLater() {
        CourseAdapter adapter = new CourseAdapter(
                this,
                CourseSaveManager.getWatchLaterList(),
                CourseAdapter.MODE_WATCH_LATER
        );
        rvCourses.setAdapter(adapter);
    }
}
