package dev.simon.PriestDashboard.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.KeyGenerator;
import dev.simon.Classes.User;
import dev.simon.Classes.Video;
import dev.simon.PriestDashboard.Fragments.MyVideosFragment;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class AddVideoActivity extends AppCompatActivity {

    private TextInputEditText vid_title, vid_des, vid_cost;
    private VideoView video_view;
    private SweetAlertDialog lDialog;
    private ImageView thumbnail;
    private ConstraintLayout constraint_lay;

    private ActivityResultLauncher<Intent> launcher;

    private Uri videoUri;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    UploadTask uploadTask;

    Video video;
    String timeStamp;
    User current_user;

    KeyGenerator key_gen = new KeyGenerator(36);
    String new_video_id = key_gen.nextString(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        lDialog = new SweetAlertDialog(AddVideoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Sending data..");
        lDialog.setCancelable(false);

        vid_title = findViewById(R.id.vid_title);
        vid_des = findViewById(R.id.vid_des);
        video_view = findViewById(R.id.video_view);
        constraint_lay = findViewById(R.id.constraint_lay);
        vid_cost = findViewById(R.id.vid_cost);
        thumbnail = findViewById(R.id.thumbnail);

        MaterialButton upload_video = findViewById(R.id.upload_video);
        RoundedImageView add_video = findViewById(R.id.add_video);

        video = new Video();
        storageReference = FirebaseStorage.getInstance().getReference("Videos").child(new_video_id);
        databaseReference = FirebaseDatabase.getInstance().getReference("videos").child(new_video_id);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                videoUri = result.getData().getData();
                video_view.setVideoURI(videoUri);
                video_view.setVisibility(View.VISIBLE);
                constraint_lay.setVisibility(View.VISIBLE);

                Glide.with(getApplicationContext()).load(videoUri)
                        .placeholder(R.drawable.loading)
                        .override(2160, 1080)
                        .fitCenter()
                        .into(thumbnail);

                MediaController mediaController = new MediaController(this);
                video_view.setMediaController(mediaController);
                mediaController.setAnchorView(video_view);

                video_view.setOnPreparedListener(mediaPlayer -> {

                    thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            thumbnail.setVisibility(View.GONE);
                            video_view.isPlaying();
                        }
                    });
                    if (video_view.isPlaying()){
                        thumbnail.setVisibility(View.GONE);
                    }
                    mediaController.show();
                });

            }
        });


        add_video.setOnClickListener(View -> openGallery());

        upload_video.setOnClickListener(View -> checkFields());

    }

    private void checkFields() {

        if (videoUri == null ||
                Objects.requireNonNull(vid_title.getText()).toString().trim().equals("") ||
                vid_cost.getText().toString().trim().equals("") ||
                Objects.requireNonNull(vid_des.getText()).toString().trim().equals("")){
            lDialog = new SweetAlertDialog(AddVideoActivity.this, SweetAlertDialog.WARNING_TYPE);
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
            UploadVideo();
        }

    }

    private void UploadVideo() {
        lDialog = new SweetAlertDialog(AddVideoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);
        lDialog.show();

        String videoName = Objects.requireNonNull(vid_title.getText()).toString().trim();
        String videoDes = Objects.requireNonNull(vid_des.getText()).toString().trim();
        timeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        int videoCost = Integer.parseInt(Objects.requireNonNull(vid_cost.getText()).toString().trim());

        final StorageReference reference = storageReference.child("video" + System.currentTimeMillis() + "." + getExt(videoUri));
        uploadTask = reference.putFile(videoUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()){
                throw Objects.requireNonNull(task.getException());
            }
            return reference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Uri downloadUrl = task.getResult();
                Toast.makeText(AddVideoActivity.this, "Video saved!", Toast.LENGTH_SHORT).show();

                video.setVideo_title(videoName);
                video.setVideo_url(downloadUrl.toString());
                video.setVideo_id(new_video_id);
                video.setVideo_des(videoDes);
                video.setVideo_purchased(false);
                video.setVideo_upload_time(timeStamp);
                video.setVideo_cost(videoCost);

                databaseReference.setValue(video).addOnSuccessListener(unused -> {
                    lDialog.dismiss();
                    backToVideos();
                    Toast.makeText(AddVideoActivity.this, "Done!", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    lDialog.dismiss();
                    lDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                    lDialog.setCancelable(true);
                    lDialog.show();
                });
            }else {
                lDialog.dismiss();
                Toast.makeText(AddVideoActivity.this, "Video failed: "
                        + Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void backToVideos() {
        Intent intent = new Intent(AddVideoActivity.this, PriestDashActivity.class);
        startActivity(intent);
        finish();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);

    }

    private String getExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}