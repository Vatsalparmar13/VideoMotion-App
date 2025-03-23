package com.example.videomotion;

public class VideoFolderModel {
    private String folderName;
    private String folderPath;
    private int videoCount;

    public VideoFolderModel(String folderName, String folderPath, int videoCount) {
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.videoCount = videoCount;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }
}
