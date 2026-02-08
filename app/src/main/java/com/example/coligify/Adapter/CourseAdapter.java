package com.example.coligify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coligify.Model.CourseModel;
import com.example.coligify.R;
import com.example.coligify.Utils.CourseSaveManager;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    public static final int MODE_NORMAL = 0;
    public static final int MODE_FAVOURITE = 1;
    public static final int MODE_WATCH_LATER = 2;

    private final Context context;
    private final List<CourseModel> list;
    private final int mode;

    public CourseAdapter(Context context, List<CourseModel> sourceList, int mode) {
        this.context = context;
        this.list = new ArrayList<>(sourceList); // ✅ COPY LIST
        this.mode = mode;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getTitle().hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.course_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CourseModel model = list.get(position);

        holder.imgCourse.setImageResource(model.getImage());
        holder.tvTitle.setText(model.getTitle());
        holder.tvRating.setText(model.getRating());
        holder.tvLevel.setText(model.getLevel());

        holder.btnLike.setImageResource(
                model.isLiked() ? R.drawable.heart : R.drawable.ic_like
        );

        holder.btnBookmark.setImageResource(
                model.isBookmarked() ? R.drawable.boolmark : R.drawable.ribbon
        );

        // ================= ICON VISIBILITY =================
        if (mode == MODE_FAVOURITE) {
            holder.btnLike.setVisibility(View.VISIBLE);
            holder.btnBookmark.setVisibility(View.GONE);
        } else if (mode == MODE_WATCH_LATER) {
            holder.btnLike.setVisibility(View.GONE);
            holder.btnBookmark.setVisibility(View.VISIBLE);
        } else {
            // MODE_NORMAL
            holder.btnLike.setVisibility(View.VISIBLE);
            holder.btnBookmark.setVisibility(View.VISIBLE);
        }

        // ================= ❤️ LIKE =================
        holder.btnLike.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION || pos >= list.size()) return;

            CourseModel item = list.get(pos);

            if (mode == MODE_FAVOURITE) {
                item.setLiked(false);
                CourseSaveManager.removeFromFavourite(context, item);
                list.remove(pos);
                notifyItemRemoved(pos);
            } else {
                item.setLiked(!item.isLiked());
                if (item.isLiked()) {
                    CourseSaveManager.addToFavourite(context, item);
                } else {
                    CourseSaveManager.removeFromFavourite(context, item);
                }
                notifyItemChanged(pos);
            }
        });

        // ================= 🔖 BOOKMARK =================
        holder.btnBookmark.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION || pos >= list.size()) return;

            CourseModel item = list.get(pos);

            if (mode == MODE_WATCH_LATER) {
                item.setBookmarked(false);
                CourseSaveManager.removeFromWatchLater(context, item);
                list.remove(pos);
                notifyItemRemoved(pos);
            } else {
                item.setBookmarked(!item.isBookmarked());
                if (item.isBookmarked()) {
                    CourseSaveManager.addToWatchLater(context, item);
                } else {
                    CourseSaveManager.removeFromWatchLater(context, item);
                }
                notifyItemChanged(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ================= VIEW HOLDER =================
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCourse, btnLike, btnBookmark;
        TextView tvTitle, tvRating, tvLevel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCourse = itemView.findViewById(R.id.imgCourse);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvLevel = itemView.findViewById(R.id.tvLevel);
        }
    }
}
