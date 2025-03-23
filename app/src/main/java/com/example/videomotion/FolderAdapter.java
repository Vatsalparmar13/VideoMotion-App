package com.example.videomotion;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.CustomViewHolder> {

    private final Context context;
    private final List<VideoFolderModel> folderList;

    public FolderAdapter(Context context, List<VideoFolderModel> folderList) {
        this.context = context;
        this.folderList = folderList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        VideoFolderModel folder = folderList.get(position);
        // Extract only the folder name from the full path
        String folderName = folder.getFolderPath().substring(folder.getFolderPath().lastIndexOf("/") + 1);

        holder.txtFolderName.setText(folderName); // Set extracted folder name

        holder.txtVideos.setText(String.valueOf(folder.getVideoCount())); // Fixed int to String

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoListActivity.class);
            intent.putExtra("FOLDER_PATH", folder.getFolderPath());  // Pass folder path
            intent.putExtra("FOLDER_NAME", folderName);  // Fixed key name
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView txtFolderName, txtVideos;
        ImageView folderImage;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            folderImage = itemView.findViewById(R.id.folderImage);
            txtFolderName = itemView.findViewById(R.id.txtfolderName);
            txtVideos = itemView.findViewById(R.id.txtvideos);
        }
    }
}
