package dev.simon.PriestDashboard.EditActivities;

import static com.google.android.gms.common.util.CollectionUtils.mapOf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Video;
import dev.simon.PriestDashboard.Adapters.MyVideoAdapter;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class EditVideo extends AppCompatActivity {

    private MaterialButton update_video;
    private TextInputEditText edt_vid_des, edt_vid_title;
    private VideoView edt_video_view;
    private SweetAlertDialog lDialog;

    MyVideoAdapter myVideoAdapter;
    DatabaseReference myRef;
    FirebaseDatabase database;

    String video_id;
    String video_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);

        update_video = findViewById(R.id.update_video);
        edt_vid_des = findViewById(R.id.edt_vid_des);
        edt_vid_title = findViewById(R.id.edt_vid_title);
        edt_video_view = findViewById(R.id.edt_video_view);

        Bundle bundle = getIntent().getExtras();
        video_id = bundle.getString("video_id");
        String video_des = bundle.getString("video_des");
        String video_title = bundle.getString("video_title");
        video_uri = bundle.getString("video_url");

        Uri uri = Uri.parse(video_uri);
        edt_video_view.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        edt_video_view.setMediaController(mediaController);
        mediaController.setAnchorView(edt_video_view);
        edt_video_view.setVisibility(View.VISIBLE);

        edt_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaController.show();
            }
        });

        edt_vid_des.setText(video_des);
        edt_vid_title.setText(video_title);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("videos").child(video_id);

        update_video.setOnClickListener(View -> fetchDetails(myRef));

    }

    private void fetchDetails(DatabaseReference myRef) {

        myRef.child("video_des").setValue(edt_vid_des.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                lDialog = new SweetAlertDialog(EditVideo.this, SweetAlertDialog.SUCCESS_TYPE);
                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                lDialog.setTitleText("Video description updated successfully!");
                lDialog.setCancelable(true);
                lDialog.show();
                lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        lDialog.show();
                        startActivity(new Intent(EditVideo.this, PriestDashActivity.class));
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                lDialog = new SweetAlertDialog(EditVideo.this, SweetAlertDialog.ERROR_TYPE);
                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                lDialog.setCancelable(true);
                lDialog.show();
            }
        });

    }
}