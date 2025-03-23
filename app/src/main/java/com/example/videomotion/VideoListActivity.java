package com.example.videomotion;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    LinearLayout titleFolder;
    ImageView imgback;
    TextView txttitlefoldername;
    RecyclerView videosRecyView;
    private VideoAdapter videoAdapter;
    private List<VideoModel> videoList;
    String foldernm,folderpt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_list);
        // Status Bar Customization
        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(decorView);
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(false);
        }
        getWindow().setStatusBarColor(getResources().getColor(R.color.lightBlue));

        titleFolder=findViewById(R.id.titleFolder);
        imgback=findViewById(R.id.imgBackArrow);
        txttitlefoldername=findViewById(R.id.txtFolderNameTitle);
        videosRecyView=findViewById(R.id.videosRecyView);

        videosRecyView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent=getIntent();
        foldernm=intent.getStringExtra("FOLDER_NAME");
        folderpt=intent.getStringExtra("FOLDER_PATH");

        txttitlefoldername.setText(foldernm);

        if (folderpt != null) {
            fetchVideos();
        } else {
            Toast.makeText(this, "Folder path not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void fetchVideos() {
        videoList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.DURATION
        };

        // Fix selection issue
        String selection = MediaStore.Video.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{folderpt + "/%"};

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String resolution = cursor.getString(4);
                String duration = cursor.getString(5);

                videoList.add(new VideoModel(id, path, title, size, resolution, duration));
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Toast.makeText(this, "No videos found in this folder!", Toast.LENGTH_SHORT).show();
        }

        // Set adapter
        videoAdapter = new VideoAdapter(this, videoList);
        videosRecyView.setAdapter(videoAdapter);
    }

}