package dev.simon.UserDashboard.Payments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Video;
import dev.simon.UserDashboard.Activities.VideoPlayActivity;
import dev.simon.UserDashboard.UserDashActivity;
import dev.simon.behold.R;
import timber.log.Timber;

public class PurchaseVideoActivity extends AppCompatActivity {

    private MaterialButton pay_pal;
    private MaterialButton pay_credit;
    private String video_clicked_id;
    private String video_clicked_title;
    private String video_clicked_url;
    private SweetAlertDialog lDialog;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_video);

        db = FirebaseFirestore.getInstance();

        TextView vid_title = findViewById(R.id.vid_title);
        TextView purchase_cost = findViewById(R.id.purchase_cost);
        MaterialButton pay_mpesa = findViewById(R.id.pay_mpesa);
        pay_pal = findViewById(R.id.pay_pal);
        pay_credit = findViewById(R.id.pay_credit);

        Bundle bundle = getIntent().getExtras();
        video_clicked_id = bundle.getString("video_clicked_id");
        String video_cost = bundle.getString("video_cost");
        video_clicked_title = bundle.getString("video_clicked_title");
        video_clicked_url = bundle.getString("video_clicked_url");

        purchase_cost.setText(video_cost);
        vid_title.setText(video_clicked_title);

        pay_mpesa.setOnClickListener(view -> unlockVideo());

    }

    private void unlockVideo() {

        lDialog = new SweetAlertDialog(PurchaseVideoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Unlocking videos. Please wait!");
        lDialog.setCancelable(false);
        lDialog.show();

        Video pur_vid = new Video();
        pur_vid.setVideo_id(video_clicked_id);
        pur_vid.setVideo_title(video_clicked_title);

        db.collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("videos_purchased")
                .document( video_clicked_id)
                .set(pur_vid)
                .addOnSuccessListener(unused -> {

                    lDialog.dismiss();
                    showDialog();
                })
                .addOnFailureListener(e -> {
                    Timber.tag("USER_UPDATE_DB: ").w("FAILED: %s", e.getLocalizedMessage());
                    lDialog.dismiss();

                    lDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                    lDialog.setCancelable(false);
                    lDialog.show();
                    lDialog.setConfirmClickListener(sweetAlertDialog -> {
                        lDialog.dismiss();
                        startActivity(new Intent(PurchaseVideoActivity.this, UserDashActivity.class));
                        finish();
                    });
                });

    }

    private void showDialog() {
        lDialog = new SweetAlertDialog(PurchaseVideoActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Video Purchased. Thank you and be blessed!");
        lDialog.setCancelable(false);
        lDialog.show();
        lDialog.setConfirmClickListener(sweetAlertDialog -> {
            lDialog.dismiss();
            Intent intent = new Intent(PurchaseVideoActivity.this, VideoPlayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("video_clicked_url", video_clicked_url);
            bundle.putString("video_clicked_id", video_clicked_id);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });
    }
}