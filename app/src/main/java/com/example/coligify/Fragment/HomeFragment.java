package com.example.coligify.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coligify.My_Profile_Activity;
import com.example.coligify.R;

public class HomeFragment extends Fragment {

    ImageView profileImg, micIcon, notificationIcon;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        profileImg = view.findViewById(R.id.profileImg);



        profileImg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), My_Profile_Activity.class);
            startActivity(intent);
        });

        return view;
    }
}
