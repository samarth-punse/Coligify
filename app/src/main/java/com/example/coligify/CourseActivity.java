package com.example.coligify;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coligify.Adapter.CourseAdapter;
import com.example.coligify.Model.CourseModel;
import com.example.coligify.Utils.CourseSaveManager;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private List<CourseModel> courseList;
    private CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // 🔙 Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 📋 RecyclerView
        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setHasFixedSize(true);

        // 📥 Get selected year
        int year = getIntent().getIntExtra("YEAR", 1);

        // 📦 Load courses once
        courseList = getCoursesByYear(year);

        // 🔗 Adapter (MODE_NORMAL)
        adapter = new CourseAdapter(
                this,
                courseList,
                CourseAdapter.MODE_NORMAL
        );
        rvCourses.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 🔑 Always refresh saved state
        restoreSavedState(courseList);

        // 🔄 Refresh UI
        adapter.notifyDataSetChanged();
    }

    // ===== RESTORE LIKE & BOOKMARK STATE =====
    private void restoreSavedState(List<CourseModel> courses) {

        List<CourseModel> favList = CourseSaveManager.getFavouriteList();
        List<CourseModel> watchList = CourseSaveManager.getWatchLaterList();

        for (CourseModel course : courses) {
            course.setLiked(favList.contains(course));
            course.setBookmarked(watchList.contains(course));
        }
    }

    // ===== YEAR-WISE DATA =====
    private List<CourseModel> getCoursesByYear(int year) {

        List<CourseModel> list = new ArrayList<>();

        if (year == 1) {
            list.add(new CourseModel(R.drawable.english,
                    "Communication Skills", "⭐ 4.6 (1.2k learners)", "Beginner"));
            list.add(new CourseModel(R.drawable.maths,
                    "Basic Mathematics", "⭐ 4.5 (1.0k learners)", "Beginner"));
            list.add(new CourseModel(R.drawable.physics,
                    "Basic Science (Physics)", "⭐ 4.3 (1.2k learners)", "Beginner"));
            list.add(new CourseModel(R.drawable.chemistry,
                    "Basic Science (Chemistry)", "⭐ 4.1 (1.1k learners)", "Beginner"));
            list.add(new CourseModel(R.drawable.applied_maths,
                    "Applied Mathematics", "⭐ 4.4 (1.6k learners)", "Beginner"));
            list.add(new CourseModel(R.drawable.bee,
                    "Basic Electrical and Electronics", "⭐ 4.4 (1.6k learners)", "Beginner"));
            list.add(new CourseModel(R.drawable.c_language,
                    "Programming in C", "⭐ 4.4 (1.6k learners)", "Beginner"));

        } else if (year == 2) {
            list.add(new CourseModel(R.drawable.data_structure,
                    "Data Structure Using C", "⭐ 4.7 (1.5k learners)", "Intermediate"));
            list.add(new CourseModel(R.drawable.database,
                    "Database Management System", "⭐ 4.6 (1.1k learners)", "Intermediate"));
            list.add(new CourseModel(R.drawable.dte,
                    "Digital Techniques", "⭐ 4.6 (1.1k learners)", "Intermediate"));
            list.add(new CourseModel(R.drawable.oop,
                    "Object Oriented Programming Using C++", "⭐ 4.6 (1.1k learners)", "Intermediate"));
            list.add(new CourseModel(R.drawable.java,
                    "Java Programming", "⭐ 4.6 (1.1k learners)", "Intermediate"));
            list.add(new CourseModel(R.drawable.dcn,
                    "Data Communication and Computer Network", "⭐ 4.6 (1.1k learners)", "Intermediate"));
            list.add(new CourseModel(R.drawable.mic,
                    "Microprocessor Programming", "⭐ 4.6 (1.1k learners)", "Intermediate"));

        } else if (year == 3) {
            list.add(new CourseModel(R.drawable.osy,
                    "Operating System", "⭐ 4.8 (900 learners)", "Advanced"));
            list.add(new CourseModel(R.drawable.sen,
                    "Software Engineering", "⭐ 4.5 (700 learners)", "Advanced"));
            list.add(new CourseModel(R.drawable.acn,
                    "Advance Computer Network", "⭐ 4.5 (700 learners)", "Advanced"));
            list.add(new CourseModel(R.drawable.sft,
                    "Software Testing", "⭐ 4.5 (700 learners)", "Advanced"));
            list.add(new CourseModel(R.drawable.nis,
                    "Network And Information Security", "⭐ 4.5 (700 learners)", "Advanced"));
        }

        return list;
    }
}
