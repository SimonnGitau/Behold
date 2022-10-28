package dev.simon.Authenticate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Channel;
import dev.simon.Classes.KeyGenerator;
import dev.simon.Classes.User;
import dev.simon.behold.R;
import timber.log.Timber;

public class CreateChannelAccount extends AppCompatActivity {

    private TextInputEditText church_name, church_locale;

    private SweetAlertDialog lDialog;

    private Channel channel;
    String timeStamp, userPhone;

    User current_user;

    KeyGenerator key_gen = new KeyGenerator(36);
    String new_channel_id = key_gen.nextString(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel_account);

        lDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Finishing up...");
        lDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        userPhone = bundle.getString("phone_input");

        church_name = findViewById(R.id.church_name);
        church_locale = findViewById(R.id.church_locale);
        MaterialButton chan_done = findViewById(R.id.chan_done);

        chan_done.setOnClickListener(View-> checkFields());

    }

    private void checkFields() {

        if (Objects.requireNonNull(church_name.getText()).toString().trim().equals("")
        || Objects.requireNonNull(church_locale.getText()).toString().trim().equals("")){
            Toast.makeText(this, "All fields required!", Toast.LENGTH_SHORT).show();
        }else {
            updateChannelDP();
        }

    }

    private void updateChannelDP() {
        lDialog.show();

        String churchName = Objects.requireNonNull(church_name.getText()).toString().trim();
        String churchLocale = Objects.requireNonNull(church_locale.getText()).toString().trim();
        timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Timber.tag("UPDATING_USER_DP:: ").w("....STARTED....");

        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("account_details")
                .document("user_account_details")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Timber.tag("CHANNEL_FETCH_USER:").w("SUCCESS!");
                    current_user = documentSnapshot.toObject(User.class);
                    assert current_user != null;

                    channel = new Channel();
                    channel.setChan_name(current_user.getUser_name());
                    channel.setChurch_name(churchName);
                    channel.setChurch_loc(churchLocale);
                    channel.setChan_mail(current_user.getUser_mail());
                    channel.setChan_mpesa_num(Integer.parseInt(userPhone));
                    channel.setChan_id(new_channel_id);
                    channel.setChan_dp_url(current_user.getUser_dp_url());

                    FirebaseFirestore.getInstance().collection("Channels")
                            .document(new_channel_id)
                            .set(channel)
                            .addOnSuccessListener(unused -> {
                                Timber.tag("CHANNEL_UPDATE:").w("SUCCESS!");
                                Toast.makeText(CreateChannelAccount.this, "Channel created", Toast.LENGTH_LONG).show();
                                updateUserChan();
                            }).addOnFailureListener(e -> {
                                lDialog.dismiss();
                                Timber.tag("CHANNEL_UPDATE:").w("FAIL: %s", e.getLocalizedMessage());
                                Toast.makeText(CreateChannelAccount.this, "Channel failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                }).addOnFailureListener(e -> {
                    lDialog.dismiss();
                    Timber.tag("CHANNEL_FETCH_USER:").w("FAIL: %s", e.getLocalizedMessage());
                    Toast.makeText(CreateChannelAccount.this, "Error fetching user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }

    private void updateUserChan() {

        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("account_details")
                .document("user_account_details")
                .update("user_channel_id", new_channel_id)
                .addOnSuccessListener(unused -> {
                    lDialog.dismiss();
                    Toast.makeText(CreateChannelAccount.this, "Channel ID updated!", Toast.LENGTH_LONG).show();
                    toLogIn();
                }).addOnFailureListener(e -> {
                    lDialog.dismiss();
                    Toast.makeText(CreateChannelAccount.this, "Channel ID: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                });

    }

    private void toLogIn() {
        Intent intent = new Intent(CreateChannelAccount.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

}