package dev.simon.UserDashboard.Payments;

import static dev.simon.UserDashboard.Payments.Constants.BUSINESS_SHORT_CODE;
import static dev.simon.UserDashboard.Payments.Constants.CALLBACKURL;
import static dev.simon.UserDashboard.Payments.Constants.PARTYB;
import static dev.simon.UserDashboard.Payments.Constants.PASSKEY;
import static dev.simon.UserDashboard.Payments.Constants.TRANSACTION_TYPE;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Video;
import dev.simon.UserDashboard.Activities.VideoPlayActivity;
import dev.simon.UserDashboard.Payments.Model.AccessToken;
import dev.simon.UserDashboard.Payments.Model.STKPush;
import dev.simon.UserDashboard.Payments.Services.DarajaApiClient;
import dev.simon.UserDashboard.UserDashActivity;
import dev.simon.behold.MainActivity;
import dev.simon.behold.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class PurchaseVideoActivity extends AppCompatActivity {

    private DarajaApiClient mApiClient;

    private MaterialButton pay_pal;
    private MaterialButton pay_credit;
    private String video_clicked_id;
    private String video_clicked_title;
    private String video_clicked_url;
    private SweetAlertDialog lDialog;

    private FirebaseFirestore db;
    private String nn;

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

        //pay_mpesa.setOnClickListener(view -> unlockVideo());

        nn = "254757909960";

        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.

        pay_mpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_number = nn;
                //String amount = video_cost;
                String amount = "1";
                performSTKPush(phone_number,amount);
            }
        });

        getAccessToken();

    }

    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull retrofit2.Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                    Toast.makeText(PurchaseVideoActivity.this, "AccessToken Successful!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                Toast.makeText(PurchaseVideoActivity.this, "Access Token Fail: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void performSTKPush(String phone_number,String amount) {
        Toast.makeText(this, "Sent! Please wait...", Toast.LENGTH_SHORT).show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL,
                video_clicked_title, //Account reference
                "VideoPurchase"  //Transaction description
        );

        mApiClient.setGetAccessToken(false);

        //Sending the data to the Mpesa API, remember to remove the logging when in production.
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());
                    } else {
                        Timber.e("Response %s", response.errorBody().string());Toast.makeText(PurchaseVideoActivity.this, "Payment not done!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                Timber.e(t);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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