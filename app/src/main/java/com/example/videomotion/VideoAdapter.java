package com.example.videomotion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private Context context;
    private List<VideoModel> videoList;

    public VideoAdapter(Context context, List<VideoModel> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_videos, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel video = videoList.get(position);

        if (video != null) {
            holder.txtVideoTitle.setText(video.getTitle()); // <-- This line causes the crash if txtVideoTitle is null
            holder.txtVideoDuration.setText("Duration: " + formatDuration(video.getDuration()));
            holder.txtVideoSize.setText("Size: " + formatSize(video.getSize()));
        } else {
            holder.txtVideoTitle.setText("Unknown Title"); // Provide a fallback to avoid null issues
        }
        // Load video thumbnail using Glide
        Glide.with(holder.itemView.getContext())
                .load(Uri.fromFile(new File(video.getPath()))) // Load from local storage
                .placeholder(R.drawable.video) // Placeholder while loading
                .error(R.drawable.video) // Fallback image if failed
                .centerCrop()
                .into(holder.thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                Intent intent_play=new Intent(context, VideoplayerActivity.class);
                intent_play.putExtra("VIDEO_TITLE",video.getTitle());
                intent_play.putExtra("VIDEO_PATH",getVideoPaths());
                intent_play.putExtra("VIDEO_INDEX",adapterPosition);
                context.startActivity(intent_play);
            }
        });
    }
    private ArrayList<String> getVideoPaths() {
        ArrayList<String> paths = new ArrayList<>();
        for (VideoModel video : videoList) {
            paths.add(video.getPath());
        }
        return paths;
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }
    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        TextView txtVideoTitle,txtVideoDuration, txtVideoSize;
        ImageView thumbnail;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtVideoTitle=itemView.findViewById(R.id.txtVideoTitle);
            thumbnail=itemView.findViewById(R.id.videoThumbnail);
            txtVideoDuration = itemView.findViewById(R.id.txtVideoDuration);
            txtVideoSize = itemView.findViewById(R.id.txtVideoSize);

        }
    }
    private String formatDuration(String durationStr) {
        try {
            long durationMs = Long.parseLong(durationStr);
            int seconds = (int) (durationMs / 1000) % 60;
            int minutes = (int) ((durationMs / (1000 * 60)) % 60);
            int hours = (int) ((durationMs / (1000 * 60 * 60)) % 24);
            return (hours > 0 ? hours + ":" : "") + minutes + ":" + String.format("%02d", seconds);
        } catch (Exception e) {
            return "00:00";
        }
    }

    private String formatSize(String sizeStr) {
        try {
            long sizeBytes = Long.parseLong(sizeStr);
            double sizeMB = sizeBytes / (1024.0 * 1024.0);
            return String.format("%.2f MB", sizeMB);
        } catch (Exception e) {
            return "0 MB";
        }
    }
}
