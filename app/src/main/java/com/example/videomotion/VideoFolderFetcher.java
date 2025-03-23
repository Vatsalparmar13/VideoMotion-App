package com.example.videomotion;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoFolderFetcher {

    public static List<VideoFolderModel> getVideoFolders(Context context) {
        List<VideoFolderModel> videoFolders = new ArrayList<>();
        Map<String, Integer> folderMap = new HashMap<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

            while (cursor.moveToNext()) {
                String videoPath = cursor.getString(columnIndex);
                File file = new File(videoPath);

                // Handle potential NullPointerException
                File parentFile = file.getParentFile();
                if (parentFile == null) continue;

                String folderPath = parentFile.getAbsolutePath();
                String folderName = parentFile.getName();

                folderMap.put(folderPath, folderMap.getOrDefault(folderPath, 0) + 1);
            }
            cursor.close();
        }

        for (Map.Entry<String, Integer> entry : folderMap.entrySet()) {
            videoFolders.add(new VideoFolderModel(entry.getKey(), entry.getKey(), entry.getValue()));
        }

        return videoFolders;
    }
}
