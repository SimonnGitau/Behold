package dev.simon.PriestDashboard.EditActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.User;
import dev.simon.PriestDashboard.Activities.AddYTVideoActivity;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class EditYou extends AppCompatActivity {

    private TextInputEditText edt_you_link, edt_yt_des;
    private YouTubePlayerView edt_player_view;
    private MaterialButton upload_you_video;

    private SweetAlertDialog lDialog;

    DatabaseReference myRef;
    FirebaseDatabase database;

    private String you_view_id, you_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_you);

        lDialog = new SweetAlertDialog(EditYou.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        you_view_id = bundle.getString("you_view_id");
        you_id = bundle.getString("you_id");
        String you_des = bundle.getString("you_des");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SharedVideos").child(you_id);

        edt_you_link = findViewById(R.id.edt_you_link);
        upload_you_video = findViewById(R.id.upload_you_video);
        edt_yt_des = findViewById(R.id.edt_yt_des);

        edt_yt_des.setText(you_des);
        edt_you_link.setText(you_view_id);

        edt_player_view = findViewById(R.id.edt_player_view);
        getLifecycle().addObserver(edt_player_view);

        edt_player_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(you_view_id, 0);
                youTubePlayer.play();
            }
        });

        upload_you_video.setOnClickListener(View ->{
            updateYou();
        });

    }

    private void updateYou() {

        myRef.child("you_vid_des").setValue(edt_yt_des.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                lDialog = new SweetAlertDialog(EditYou.this, SweetAlertDialog.SUCCESS_TYPE);
                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                lDialog.setTitleText("The description was updated successfully!");
                lDialog.setCancelable(true);
                lDialog.show();
                lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        lDialog.dismiss();
                        onDestroy();
                        onPause();
                        onStop();
                        startActivity(new Intent(EditYou.this, PriestDashActivity.class));
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                lDialog = new SweetAlertDialog(EditYou.this, SweetAlertDialog.ERROR_TYPE);
                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                lDialog.setCancelable(true);
                lDialog.show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        edt_player_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.pause();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        edt_player_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.pause();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        edt_player_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.pause();
            }
        });
    }
}