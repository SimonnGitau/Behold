package dev.simon.Authenticate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.behold.R;

public class PassResetActivity extends AppCompatActivity {

    private TextInputEditText mail_reset;

    private SweetAlertDialog lDialog;
    private FirebaseAuth rAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_reset);

        mail_reset = findViewById(R.id.mail_reset);

        rAuth = FirebaseAuth.getInstance();

        lDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Requesting reset link...");
        lDialog.setCancelable(false);

        MaterialButton reset_btn = findViewById(R.id.reset_btn);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){

            lDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("Fetching account...");
            lDialog.setCancelable(false);
            lDialog.show();

        }


        reset_btn.setOnClickListener(View -> {
            lDialog.show();
            String mail = Objects.requireNonNull(mail_reset.getText()).toString().trim();
            rAuth.sendPasswordResetEmail(mail).addOnSuccessListener(unused -> {
                lDialog.dismiss();
                lDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                lDialog.setTitleText("Reset link has been sent to your email account");
                lDialog.setCancelable(true);
                lDialog.show();
                lDialog.setConfirmClickListener(sweetAlertDialog -> {
                    lDialog.dismiss();
                    startActivity(new Intent(PassResetActivity.this, LogInActivity.class));
                    finish();
                });

            }).addOnFailureListener(e -> {
                lDialog.dismiss();
                lDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                lDialog.setTitleText(e.getLocalizedMessage());
                lDialog.setCancelable(true);
                lDialog.show();

            });

        });

    }
}