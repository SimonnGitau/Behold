package dev.simon.PriestDashboard.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.KeyGenerator;
import dev.simon.Classes.User;
import dev.simon.Classes.VidYou;
import dev.simon.PriestDashboard.EditActivities.EditYou;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class AddYTVideoActivity extends AppCompatActivity {

    private TextInputEditText vid_link, yt_vid_des;
    private YouTubePlayerView youTubePlayerView;
    private MaterialButton upload_yt_video;

    private SweetAlertDialog lDialog;

    KeyGenerator key_gen = new KeyGenerator(36);
    String new_yt_vid_id = key_gen.nextString(0);

    User current_user;
    String timeStamp;
    String videoId;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ytvideo);

        lDialog = new SweetAlertDialog(AddYTVideoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);

        vid_link = findViewById(R.id.vid_link);
        upload_yt_video = findViewById(R.id.upload_yt_video);
        yt_vid_des = findViewById(R.id.yt_vid_des);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        if (Objects.requireNonNull(vid_link.getText()).toString().trim().length() == 11){

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    videoId = vid_link.getText().toString().trim();
                    youTubePlayer.loadVideo(videoId, 0);
                    youTubePlayer.play();
                }
            });
        }

        upload_yt_video.setOnClickListener(View->{
            checkFields();
        });

    }

    private void checkFields() {
        if (vid_link.getText().toString().trim().equals("")){
            lDialog = new SweetAlertDialog(AddYTVideoActivity.this, SweetAlertDialog.WARNING_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("All fields required!");
            lDialog.setCancelable(false);
            lDialog.show();
            lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    lDialog.dismiss();
                }
            });
        }else {
            uploadLink();
        }
    }

    private void uploadLink() {
        lDialog = new SweetAlertDialog(AddYTVideoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);
        lDialog.show();
        timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());
        VidYou vidYou = new VidYou();

        databaseReference = FirebaseDatabase.getInstance().getReference("SharedVideos").child(new_yt_vid_id);
        vidYou.setYou_vid_id(new_yt_vid_id);
        vidYou.setYou_vid_upload_time(timeStamp);
        vidYou.setYou_id(vid_link.getText().toString().trim());
        vidYou.setYou_vid_des(Objects.requireNonNull(yt_vid_des.getText()).toString().trim());

        databaseReference.setValue(vidYou).addOnSuccessListener(unused -> {
            lDialog.dismiss();
            //backToVideos();
            lDialog = new SweetAlertDialog(AddYTVideoActivity.this, SweetAlertDialog.SUCCESS_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("Video added successfully!");
            lDialog.setCancelable(true);
            lDialog.show();
            lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    lDialog.dismiss();
                    startActivity(new Intent(AddYTVideoActivity.this, PriestDashActivity.class));
                    finish();
                }
            });
            Toast.makeText(AddYTVideoActivity.this, "Done!", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.pause();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.pause();
            }
        });
    }

}