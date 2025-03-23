package com.example.videomotion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VideoplayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private ArrayList<String> video_path;
    private int currentVideoIndex = 0;
    boolean isOpen=false;
    private TextView video_title,endTime;
    private boolean isSubtitlesOn = false;
    private int rotationAngle = 0;
    private SeekBar videoSeekBar;
    private ImageView playPauseBtn, rewindBtn, forwardBtn;
    LinearLayout one,two,three, rotateBtn, subtitleBtn;
    RelativeLayout zoom_ly;
    private Handler handler = new Handler();
    ImageView img_back2videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        // Status Bar Customization
        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(decorView);
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(false);
        }
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));


        videoView = findViewById(R.id.videoView);
        img_back2videos=findViewById(R.id.videoView_go_back);
        one=findViewById(R.id.videoView_one_layout);
        two=findViewById(R.id.videoView_two_layout);
        three=findViewById(R.id.videoView_three_layout);
        zoom_ly=findViewById(R.id.zoom_layout);
        video_title=findViewById(R.id.videoView_title);

        playPauseBtn = findViewById(R.id.videoView_play_pause_btn);
        rewindBtn = findViewById(R.id.videoView_rewind);
        forwardBtn = findViewById(R.id.videoView_forward);
        rotateBtn = findViewById(R.id.videoView_rotation);
        subtitleBtn = findViewById(R.id.videoView_track);
        videoSeekBar = findViewById(R.id.videoView_seekbar);
        endTime = findViewById(R.id.videoView_endtime);

        Intent in=getIntent();
        String title_video=in.getStringExtra("VIDEO_TITLE");
        video_title.setText(title_video);

        zoom_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen){
                    hideControls();
                    isOpen=false;
                }else {
                    showControls();
                    isOpen=true;
                }
            }
        });

        //play and pause button
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });



        // Video Completion Listener
        videoView.setOnCompletionListener(mp -> playPauseBtn.setImageResource(R.drawable.ic_play));




        img_back2videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        Intent intent = getIntent();
        video_path = intent.getStringArrayListExtra("VIDEO_PATH");
        currentVideoIndex = intent.getIntExtra("VIDEO_INDEX", 0);
        if (video_path != null && !video_path.isEmpty()) {
            playVideo();
        }



        // rewind btn-go back video 10 seconds
        rewindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = videoView.getCurrentPosition();
                int rewindTime = 10000; // 10 seconds

                // Ensure we don't rewind past the start
                int newPosition = Math.max(currentPosition - rewindTime, 0);
                videoView.seekTo(newPosition);
            }
        });

        // forward btn-skip forward video 10 seconds
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = videoView.getCurrentPosition();
                int forwardTime = 10000; // 10 seconds

                // Ensure we don't forward past the video duration
                int newPosition = Math.min(currentPosition + forwardTime, videoView.getDuration());
                videoView.seekTo(newPosition);
            }
        });
        // Rotate Button - Rotate video by 90 degrees each time
        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT){
                    //set in landscape
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
                    //set in portrait
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });
        initializeSeekBars();
    }

    private void playVideo() {
        String path = video_path.get(currentVideoIndex);
        videoView.setVideoPath(path);
        videoView.setOnPreparedListener(mp -> videoView.start());
    }
    private void hideControls(){
        one.setVisibility(View.INVISIBLE);
        two.setVisibility(View.INVISIBLE);
        three.setVisibility(View.INVISIBLE);
    }
    private void showControls(){
        one.setVisibility(View.VISIBLE);
        two.setVisibility(View.VISIBLE);
        three.setVisibility(View.VISIBLE);
    }

    private void togglePlayPause() {
        if (videoView.isPlaying()) {
            videoView.pause();
            playPauseBtn.setImageResource(R.drawable.ic_play);
        } else {
            videoView.start();
            playPauseBtn.setImageResource(R.drawable.ic_pause);
        }
    }
    // Method to Update SeekBar and Current Time
    private void initializeSeekBars() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoSeekBar.setMax(videoView.getDuration()); // Set max duration
                updateSeekBar(); // Start SeekBar update
                videoView.start(); // Auto-start video playback
                subtitleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkMultiAudioTrack(mp);
                    }
                });

            }
        });

        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress); // Seek to user-selected position
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Update play/pause button when video completes
        videoView.setOnCompletionListener(mp -> playPauseBtn.setImageResource(R.drawable.ic_play));
    }
    //seekbar progress/set time
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoView.isPlaying()) {
                    int currentPosition = videoView.getCurrentPosition();
                    videoSeekBar.setProgress(currentPosition);
                    int remainingTime = videoView.getDuration() - currentPosition;
                    endTime.setText(convertIntoTime(remainingTime)); // Update remaining time
                }
                handler.postDelayed(this, 500); // Update every 500ms
            }
        }, 500);
    }

    private String convertIntoTime(int ms) {
        int seconds = (ms / 1000) % 60;
        int minutes = (ms / (1000 * 60)) % 60;
        int hours = (ms / (1000 * 60 * 60)) % 24;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }


    //Audio & Subtitle method
    private void checkMultiAudioTrack(MediaPlayer mediaPlayer) {
        MediaPlayer.TrackInfo[] trackInfos = mediaPlayer.getTrackInfo();

        ArrayList<Integer> audioTracksIndex = new ArrayList<>();

        for (int i = 0; i < trackInfos.length; i++) {
            if (trackInfos[i].getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                audioTracksIndex.add(i);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(VideoplayerActivity.this);
        builder.setTitle("Select Audio Track");

        String[] values = new String[audioTracksIndex.size()];
        for (int i = 0; i < audioTracksIndex.size(); i++) {
            values[i] = "Track " + i;
        }
        /*
         * SingleChoice means RadioGroup
         * */
        builder.setSingleChoiceItems(values, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mediaPlayer.selectTrack(which);
                Toast.makeText(VideoplayerActivity.this, "Track " + which + " Selected", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaPlayer.getSelectedTrack(which);
                }
                mediaPlayer.start();
                Toast.makeText(VideoplayerActivity.this, "we are working on that", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


}