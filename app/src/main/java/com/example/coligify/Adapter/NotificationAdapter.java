package com.example.coligify.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coligify.Model.NotificationModel;
import com.example.coligify.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<NotificationModel> notificationList;

    public NotificationAdapter(ArrayList<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NotificationModel model = notificationList.get(position);

        holder.tvTitle.setText(model.getTitle());
        holder.tvMessage.setText(model.getMessage());
        holder.tvTime.setText(model.getTime());

        // 🔥 Icon based on notification type
        switch (model.getType()) {
            case NotificationModel.TYPE_SUCCESS:
                holder.ivIcon.setImageResource(R.drawable.ic_success);
                break;

            case NotificationModel.TYPE_ERROR:
                holder.ivIcon.setImageResource(R.drawable.ic_error);
                break;

            case NotificationModel.TYPE_UPDATE:
                holder.ivIcon.setImageResource(R.drawable.ic_update);
                break;

            case NotificationModel.TYPE_ALERT:
                holder.ivIcon.setImageResource(R.drawable.ic_alert);
                break;

            default:
                holder.ivIcon.setImageResource(R.drawable.ic_notification);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvTitle, tvMessage, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
