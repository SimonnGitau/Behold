package dev.simon.Authenticate;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import dev.simon.UserDashboard.UserDashActivity;
import dev.simon.behold.AuthActivity;
import dev.simon.behold.MainActivity;
import dev.simon.behold.R;
import timber.log.Timber;

public class VerifyOTP extends AppCompatActivity {

    // string for storing our verification ID
    private String verificationId;

    // variable for FirebaseAuth class
    private FirebaseAuth mAuth;
    private SweetAlertDialog lDialog;
    PhoneAuthCredential credential;
    private CircleImageView user_dp;
    private MaterialButton verify;

    String userPhone, userCountry, gender, userName;
    Uri image_uri;

    TextView dp_error, text_View;
    private LinearLayout rel_lay;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        user_dp = findViewById(R.id.user_dp);
        dp_error = findViewById(R.id.dp_error);
        rel_lay = findViewById(R.id.rel_lay);
        text_View = findViewById(R.id.text_View);

        verify = findViewById(R.id.verify);

        lDialog = new SweetAlertDialog(VerifyOTP.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Verifying...");
        lDialog.setCancelable(true);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                image_uri = result.getData().getData();
                Timber.tag("LOADING_USER_DP:: ").w("SUCCESS");
                Picasso.get().load(image_uri).into(user_dp);
            }
        });

        Bundle bundle = getIntent().getExtras();
        String phone = bundle.getString("phone_input");
        userPhone = bundle.getString("phone_input");
        userCountry = bundle.getString("user_country");
        gender = bundle.getString("user_gender");
        userName = bundle.getString("user_name");

        mAuth = FirebaseAuth.getInstance();

        user_dp.setOnClickListener(View -> openGallery());

        verify.setOnClickListener(View -> checkFields(phone));

    }


    private void sendVerificationCode(String number) {
        lDialog.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String verificationID,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Timber.tag(TAG).d("onCodeSent:%s", verificationID);

            verificationId = verificationID;
            PhoneAuthProvider.ForceResendingToken mResendToken = token;
            verifyCode(verificationID);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            credential = PhoneAuthProvider.getCredential(verificationId, code);
            verifyCode(code);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            lDialog = new SweetAlertDialog(VerifyOTP.this, SweetAlertDialog.ERROR_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
            lDialog.setCancelable(true);
            lDialog.show();
            lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    lDialog.dismiss();
                    returnBack();
                }
            });
        }
    };

    private void returnBack() {
        startActivity(new Intent(VerifyOTP.this, UpdateProfile.class));
        finish();
    }


    private void verifyCode(String code) {

        String finalCode = code;

        if (finalCode.equals(verificationId)){
            lDialog.dismiss();
            lDialog = new SweetAlertDialog(VerifyOTP.this, SweetAlertDialog.SUCCESS_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("Verified!");
            lDialog.setCancelable(true);
            lDialog.show();
            lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    lDialog.dismiss();
                    text_View.setVisibility(View.GONE);
                    rel_lay.setVisibility(View.VISIBLE);
                    updateUser(image_uri);
                }
            });
        }else {
            lDialog.dismiss();
            lDialog = new SweetAlertDialog(VerifyOTP.this, SweetAlertDialog.ERROR_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("Please enter an active phone number!");
            lDialog.setCancelable(true);
            lDialog.show();
        }
        
    }

    private void checkFields(String phone) {
        if (user_dp.getDrawable() == null){
            dp_error.setVisibility(View.VISIBLE);
            dp_error.requestFocus();
        }else {
            sendVerificationCode(phone);
        }
    }

    private void updateUser(Uri image_uri) {

        lDialog.dismiss();

        lDialog = new SweetAlertDialog(VerifyOTP.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Updating profile");
        lDialog.setCancelable(false);
        lDialog.show();

        Timber.tag("UPDATING_USER_DP:: ").w("....STARTED....");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        final StorageReference img_name = storageReference
                .child("dp_img");

        img_name.putFile(image_uri).addOnSuccessListener(taskSnapshot ->
                img_name.getDownloadUrl().addOnSuccessListener(uri -> {
                    Toast.makeText(VerifyOTP.this, "DP updated success!",
                            Toast.LENGTH_LONG).show();

                    FirebaseFirestore.getInstance().collection("Users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("account_details")
                            .document("user_account_details")
                            .update("user_country", userCountry,
                                    "user_gender", gender,
                                    "user_channel", false,
                                    "user_phone", userPhone,
                                    "user_name", userName,
                                    "user_dp_url", uri.toString())
                            .addOnSuccessListener(unused -> {
                                lDialog.dismiss();
                                Toast.makeText(VerifyOTP.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                                nextStep();

                            }).addOnFailureListener(e -> {
                                lDialog.dismiss();
                                Timber.tag("CREATING_USER_WITH_EMAIL: ").w(MessageFormat.format("FAILED: {0}", e.getLocalizedMessage()));
                                lDialog = new SweetAlertDialog(VerifyOTP.this, SweetAlertDialog.ERROR_TYPE);
                                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                                lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                                lDialog.setCancelable(true);
                                lDialog.show();
                                lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        returnBack();
                                    }
                                });

                            });

                }).addOnFailureListener(e -> {
                    lDialog.dismiss();
                    Timber.tag("CREATING_USER_WITH_EMAIL: ").w(MessageFormat.format("FAILED: {0}", Objects.requireNonNull(e.getLocalizedMessage())));
                    lDialog = new SweetAlertDialog(VerifyOTP.this, SweetAlertDialog.ERROR_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                    lDialog.setCancelable(true);
                    lDialog.show();
                }));
    }

    private void nextStep() {
        startActivity(new Intent(VerifyOTP.this, AuthActivity.class));
        finish();
    }

    private void toChannelSetup() {
        Intent intent = new Intent(VerifyOTP.this, CreateChannelAccount.class);
        Bundle bundle = new Bundle();
        bundle.putString("phone_input", userPhone);
        startActivity(intent);
        finish();
    }

    private void openGallery() {
        Timber.tag("LOADING_USER_DP:: ").w("OPENING GALLERY");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);

    }

}