package com.example.coligify.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coligify.CourseActivity;
import com.example.coligify.My_Profile_Activity;
import com.example.coligify.R;

public class HomeFragment extends Fragment {

    // Header
    private ImageView profileImg, notificationIcon;

    // Year Chips
    private TextView firstYear, secondYear, thirdYear;

    // Static Course Cards
    private View cardSft, cardJava;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ===== INIT VIEWS =====
        profileImg = view.findViewById(R.id.profileImg);
        notificationIcon = view.findViewById(R.id.notificationIcon);

        firstYear = view.findViewById(R.id.firstYear);
        secondYear = view.findViewById(R.id.secondYear);
        thirdYear = view.findViewById(R.id.thirdYear);

        cardSft = view.findViewById(R.id.cardSft);
        cardJava = view.findViewById(R.id.cardJava);

        // Default selection
        selectYear(1);

        // ===== HEADER CLICKS =====
        profileImg.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), My_Profile_Activity.class))
        );

        notificationIcon.setOnClickListener(v ->
                Toast.makeText(getActivity(), "No new notifications 🔔", Toast.LENGTH_SHORT).show()
        );

        // ===== YEAR CHIP CLICKS =====
        firstYear.setOnClickListener(v -> openCourseActivity(1));

        secondYear.setOnClickListener(v -> openCourseActivity(2));

        thirdYear.setOnClickListener(v -> openCourseActivity(3));

        // ===== STATIC CARD CLICKS =====
        if (cardSft != null) {
            cardSft.setOnClickListener(v ->
                    Toast.makeText(getActivity(), "SFT Chapter 1 selected", Toast.LENGTH_SHORT).show()
            );
        }

        if (cardJava != null) {
            cardJava.setOnClickListener(v ->
                    Toast.makeText(getActivity(), "Java Chapter 1 selected", Toast.LENGTH_SHORT).show()
            );
        }

        return view;
    }

    // ===== OPEN COURSE ACTIVITY =====
    private void openCourseActivity(int year) {
        selectYear(year);

        Intent intent = new Intent(getActivity(), CourseActivity.class);
        intent.putExtra("YEAR", year); // optional but useful
        startActivity(intent);
    }

    // ===== YEAR SELECTION UI =====
    private void selectYear(int year) {

        firstYear.setBackgroundResource(R.drawable.chip_unselected);
        secondYear.setBackgroundResource(R.drawable.chip_unselected);
        thirdYear.setBackgroundResource(R.drawable.chip_unselected);

        if (year == 1) firstYear.setBackgroundResource(R.drawable.chip_selected);
        if (year == 2) secondYear.setBackgroundResource(R.drawable.chip_selected);
        if (year == 3) thirdYear.setBackgroundResource(R.drawable.chip_selected);
    }
}
