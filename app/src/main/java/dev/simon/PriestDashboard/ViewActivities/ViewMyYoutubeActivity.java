package dev.simon.PriestDashboard.ViewActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.PriestDashboard.EditActivities.EditVideo;
import dev.simon.PriestDashboard.EditActivities.EditYou;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class ViewMyYoutubeActivity extends AppCompatActivity {

    private YouTubePlayerView vYou_view;
    private TextView vYou_des;
    private MaterialButton edit_you, delete_you;

    private SweetAlertDialog lDialog;

    private String you_view_id, you_id, you_des, videoId;

    DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_youtube);

        vYou_view = findViewById(R.id.vYou_view);
        vYou_des = findViewById(R.id.vYou_des);
        edit_you = findViewById(R.id.edit_you);
        delete_you = findViewById(R.id.delete_you);

        Bundle bundle = getIntent().getExtras();
        you_id = bundle.getString("you_id");
        you_des = bundle.getString("you_des");
        you_view_id = bundle.getString("you_view_id");

        vYou_des.setText(you_des);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SharedVideos").child(you_id);

        getLifecycle().addObserver(vYou_view);

        vYou_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                videoId = you_view_id;
                youTubePlayer.loadVideo(videoId, 0);
                youTubePlayer.play();
            }
        });

        delete_you.setOnClickListener(View ->{
            deleteYou();
        });

        edit_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewMyYoutubeActivity.this, EditYou.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("you_id", you_id);
                bundle1.putString("you_des", you_des);
                bundle1.putString("you_view_id", you_view_id);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });
    }

    private void deleteYou() {

        lDialog = new SweetAlertDialog(ViewMyYoutubeActivity.this, SweetAlertDialog.WARNING_TYPE);
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
                        for (DataSnapshot entity : dataSnapshot.getChildren()) {
                            entity.getRef().removeValue();
                            lDialog.dismiss();

                            lDialog = new SweetAlertDialog(ViewMyYoutubeActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                            lDialog.setTitleText("Video removed!");
                            lDialog.setCancelable(true);
                            lDialog.show();
                            lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    onDestroy();
                                    onPause();
                                    onStop();
                                    lDialog.dismiss();
                                    startActivity(new Intent(ViewMyYoutubeActivity.this, PriestDashActivity.class));
                                    finish();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError e) {
                        lDialog = new SweetAlertDialog(ViewMyYoutubeActivity.this, SweetAlertDialog.ERROR_TYPE);
                        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        lDialog.setTitleText(e.getMessage());
                        lDialog.setCancelable(true);
                        lDialog.show();
                    }

                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vYou_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(you_view_id, 0);
                youTubePlayer.pause();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        vYou_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(you_view_id, 0);
                youTubePlayer.pause();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        vYou_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(you_view_id, 0);
                youTubePlayer.pause();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        vYou_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(you_view_id, 0);
                youTubePlayer.play();
            }
        });
    }
}