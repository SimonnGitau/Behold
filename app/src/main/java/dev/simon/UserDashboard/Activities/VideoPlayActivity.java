package dev.simon.UserDashboard.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Video;
import dev.simon.behold.R;

public class VideoPlayActivity extends AppCompatActivity {

    DatabaseReference myRef;
    DatabaseReference myRef2;
    FirebaseDatabase database;

    String video_clicked_id, video_clicked_url;

    private VideoView play_view;
    private TextView play_des, play_title, play_views, play_uptime;

    private SweetAlertDialog lDialog;

    private LottieAnimationView video_log;

    private int views_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        lDialog = new SweetAlertDialog(VideoPlayActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Setting up..");
        lDialog.setCancelable(false);
        lDialog.show();

        play_des = findViewById(R.id.play_des);
        video_log = findViewById(R.id.video_log);
        play_title = findViewById(R.id.play_title);
        play_views = findViewById(R.id.play_views);
        play_uptime = findViewById(R.id.play_uptime);
        play_view = findViewById(R.id.play_view);

        Bundle bundle = getIntent().getExtras();
        video_clicked_id = bundle.getString("video_clicked_id");
        video_clicked_url = bundle.getString("video_clicked_url");

        database = FirebaseDatabase.getInstance();
        myRef2 = database.getReference("videos").child(video_clicked_id);

        Toast.makeText(this, "Long press the video to watch on fullscreen.", Toast.LENGTH_LONG).show();

        setVideoView(video_clicked_url);

        setDetails();

    }

    private void toFullScreen(String video_clicked_url) {
        Intent intent = new Intent(VideoPlayActivity.this, WatchFullActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("video_id", video_clicked_url);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }

    private void setDetails() {
        myRef2.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    Video current_video1 = dataSnapshot.getValue(Video.class);
                    assert current_video1 != null;
                    play_des.setText(current_video1.getVideo_des());
                    play_title.setText(current_video1.getVideo_title());
                    play_uptime.setText(String.valueOf(current_video1.getVideo_upload_time()));
                    play_views.setText(String.valueOf(current_video1.getVideo_views()));
                    views_num = current_video1.getVideo_views();
                }
            }
        });
    }

    private void setVideoView(String video_clicked_url) {
        Uri uri = Uri.parse(video_clicked_url);
        play_view.setVideoURI(uri);

        MediaController mediaController = new MediaController(VideoPlayActivity.this);
        play_view.setMediaController(mediaController);
        mediaController.setAnchorView(play_view);
        mediaController.setDrawingCacheBackgroundColor(ContextCompat.getColor(VideoPlayActivity.this, R.color.blue));

        play_view.setVisibility(View.VISIBLE);

        play_view.setOnLongClickListener(view -> {
            toFullScreen(video_clicked_url);
            return true;
        });

        play_view.setOnCompletionListener(mediaPlayer -> mediaController.show());

        lDialog.dismiss();

        play_view.setOnPreparedListener(mediaPlayer -> {
            play_view.start();
            video_log.setVisibility(View.GONE);
            mediaController.show();
            finalUpdate(views_num);
        });
    }


    private void finalUpdate(int views_num) {

        int fin_views = views_num + 1;

        myRef2.child("video_views").setValue(fin_views).addOnSuccessListener(unused -> {
            setDetails();

            /*lDialog = new SweetAlertDialog(VideoPlayActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("View added");
            lDialog.setCancelable(true);
            lDialog.show();
            lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    lDialog.dismiss();
                    setDetails();
                }
            });*/
        }).addOnFailureListener(e -> {
            /*lDialog = new SweetAlertDialog(VideoPlayActivity.this, SweetAlertDialog.ERROR_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
            lDialog.setCancelable(true);
            lDialog.show();*/
        });

}
}