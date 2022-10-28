package dev.simon.PriestDashboard.ViewActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Video;
import dev.simon.PriestDashboard.Adapters.MyVideoAdapter;
import dev.simon.PriestDashboard.EditActivities.EditVideo;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class PlayMyVideoActivity extends AppCompatActivity {

    private VideoView play_my_view;
    private TextView play_view_title, play_view_des;
    private SweetAlertDialog lDialog;
    private MaterialButton play_view_edit, play_view_remove;

    DatabaseReference myRef;
    FirebaseDatabase database;

    String video_url;
    String video_id;

    MyVideoAdapter myVideoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_my_video);

        Bundle bundle = getIntent().getExtras();
        video_url = bundle.getString("video_clicked_url");
        String video_title = bundle.getString("video_title");
        String video_context = bundle.getString("video_context");
        video_id = bundle.getString("video_id");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("videos").child(video_id);

        lDialog = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);

        play_my_view = findViewById(R.id.play_my_view);
        play_view_title = findViewById(R.id.play_view_title);
        play_view_edit = findViewById(R.id.play_view_edit);
        play_view_des = findViewById(R.id.play_view_des);
        play_view_remove = findViewById(R.id.play_view_remove);

        play_view_remove.setOnClickListener(View -> deleteVideo());

        play_view_edit.setOnClickListener(View ->{
            Intent intent = new Intent(PlayMyVideoActivity.this, EditVideo.class);
            Bundle bundle1 = new Bundle();
            bundle1.putString("video_id", video_id);
            bundle1.putString("video_des", video_context);
            bundle1.putString("video_title", video_title);
            bundle1.putString("video_url", String.valueOf(video_url));
            intent.putExtras(bundle1);
            startActivity(intent);
        });

        play_view_title.setText(video_title);
        play_view_des.setText(video_context);


        Uri uri = Uri.parse(video_url);
        play_my_view.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        play_my_view.setMediaController(mediaController);
        mediaController.setAnchorView(play_my_view);
        play_my_view.setVisibility(View.VISIBLE);

        play_my_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaController.show();
            }
        });

    }

    private void deleteVideo() {

        lDialog = new SweetAlertDialog(PlayMyVideoActivity.this, SweetAlertDialog.WARNING_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Warning! Are your sure you want to delete this video and it's contents?");
        lDialog.setCancelable(true);
        lDialog.isShowCancelButton();
        lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot entity: dataSnapshot.getChildren()) {
                            entity.getRef().removeValue();
                            lDialog.dismiss();

                            lDialog = new SweetAlertDialog(PlayMyVideoActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                            lDialog.setTitleText("Video removed!");
                            lDialog.setCancelable(true);
                            lDialog.show();
                            lDialog.setConfirmClickListener(sweetAlertDialog1 -> {
                                lDialog.dismiss();
                                startActivity(new Intent(PlayMyVideoActivity.this, PriestDashActivity.class));
                                finish();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError e) {
                        lDialog = new SweetAlertDialog(PlayMyVideoActivity.this, SweetAlertDialog.ERROR_TYPE);
                        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        lDialog.setTitleText(e.getMessage().toString());
                        lDialog.setCancelable(true);
                        lDialog.show();
                    }
                });
            }
        });
        lDialog.show();

    }
}