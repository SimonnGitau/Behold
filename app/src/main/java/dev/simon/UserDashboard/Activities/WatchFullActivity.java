package dev.simon.UserDashboard.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import dev.simon.UserDashboard.UserDashActivity;
import dev.simon.behold.R;

public class WatchFullActivity extends AppCompatActivity {

    private VideoView watch_full_view;

    String video_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WatchFullActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_watch_full);

        watch_full_view = findViewById(R.id.watch_full_view);

        Bundle bundle = getIntent().getExtras();
        video_id = bundle.getString("video_id");

        Uri uri = Uri.parse(video_id);
        watch_full_view.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        watch_full_view.setMediaController(mediaController);
        mediaController.setAnchorView(watch_full_view);
        mediaController.setDrawingCacheBackgroundColor(ContextCompat.getColor(WatchFullActivity.this, R.color.blue));

        watch_full_view.setOnCompletionListener(mediaPlayer -> mediaController.show());

        watch_full_view.setOnPreparedListener(mediaPlayer -> {
            watch_full_view.start();
            watch_full_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    startActivity(new Intent(WatchFullActivity.this, VideoPlayActivity.class));
                }
            });
            mediaController.show();
        });



    }

}