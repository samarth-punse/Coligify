package com.example.coligify.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coligify.Adapter.NotificationAdapter;
import com.example.coligify.Model.NotificationModel;
import com.example.coligify.R;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    RecyclerView rvNotifications;
    LinearLayout layoutEmpty;
    ArrayList<NotificationModel> notificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        rvNotifications = view.findViewById(R.id.rvNotifications);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        notificationList = new ArrayList<>();

        // 🔹 Example data (remove to test empty page)
        // notificationList.add("Price Alert");
        // notificationList.add("ETH Sent");

        if (notificationList.isEmpty()) {
            // Show empty notification page
            layoutEmpty.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            // Show notification list
            layoutEmpty.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);

            rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
            rvNotifications.setAdapter(new NotificationAdapter(notificationList));
        }

        return view;
    }
}
