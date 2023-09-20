
package dev.simon.UserDashboard.Payments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.behold.R;

import dev.simon.UserDashboard.Payments.Model.AccessToken;
import dev.simon.UserDashboard.Payments.Model.STKPush;
import dev.simon.UserDashboard.Payments.Services.DarajaApiClient;


import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static dev.simon.UserDashboard.Payments.Constants.BUSINESS_SHORT_CODE;
import static dev.simon.UserDashboard.Payments.Constants.CALLBACKURL;
import static dev.simon.UserDashboard.Payments.Constants.PARTYB;
import static dev.simon.UserDashboard.Payments.Constants.PASSKEY;
import static dev.simon.UserDashboard.Payments.Constants.TRANSACTION_TYPE;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class DonateActivity extends AppCompatActivity {

    private DarajaApiClient mApiClient;
    private SweetAlertDialog pDialog;

    private TextInputEditText send_amount;
    private MaterialButton send_money, paypal, pay_card;

    String nn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        nn = bundle.getString("phone_number");
        Toast.makeText(this, nn, Toast.LENGTH_SHORT).show();

        send_money = findViewById(R.id.send_money);
        send_amount = findViewById(R.id.send_amount);
        pay_card = findViewById(R.id.pay_card);
        paypal = findViewById(R.id.paypal);

        pDialog = new SweetAlertDialog(this);
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.

        send_money.setOnClickListener(this::onClick);
        getAccessToken();

    }

    private void onClick(View view) {
        //String phone_number = nn.toString();
        String phone_number = "254757909960";
        String amount = send_money.getText().toString();
        performSTKPush(phone_number,amount);
    }

    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

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
                "Behold", //Account reference
                "Behold"  //Transaction description
        );

        mApiClient.setGetAccessToken(false);

        //Sending the data to the Mpesa API, remember to remove the logging when in production.
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                pDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());
                    } else {
                        Timber.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Timber.e(t);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}