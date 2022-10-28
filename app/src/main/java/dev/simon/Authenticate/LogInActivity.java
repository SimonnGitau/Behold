package dev.simon.Authenticate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.User;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.UserDashboard.UserDashActivity;
import dev.simon.behold.R;
import timber.log.Timber;

public class LogInActivity extends AppCompatActivity {

    private TextInputEditText log_in_mail, log_in_pass;
    private TextView log_mail_error;
    private TextView log_pass_error;
    private TextView auth_error;

    private SweetAlertDialog lDialog;
    private FirebaseAuth nAuth;

    private User current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        lDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Signing In...");
        lDialog.setCancelable(false);

        nAuth = FirebaseAuth.getInstance();

        log_in_mail = findViewById(R.id.log_in_mail);
        log_in_pass = findViewById(R.id.log_in_pass);
        log_mail_error = findViewById(R.id.log_mail_error);
        log_pass_error = findViewById(R.id.log_pass_error);
        TextView forgot_pass = findViewById(R.id.forgot_pass);
        auth_error = findViewById(R.id.auth_error);
        MaterialButton log_auth = findViewById(R.id.log_auth);

        forgot_pass.setOnClickListener(View->{
            Intent intent = new Intent(LogInActivity.this, PassResetActivity.class);
            startActivity(intent);
        });

        log_auth.setOnClickListener(View -> checkFields());
    }

    private void checkFields() {
        if (Objects.requireNonNull(log_in_mail.getText()).toString().trim().equals("")){
            log_mail_error.setVisibility(View.VISIBLE);
            log_mail_error.requestFocus();
        }else if (Objects.requireNonNull(log_in_pass.getText()).toString().trim().equals("")
        || Objects.requireNonNull(log_in_pass.getText()).toString().trim().length() < 6){
            log_pass_error.setVisibility(View.VISIBLE);
            log_pass_error.requestFocus();
        }else {
            log_pass_error.setVisibility(View.GONE);
            log_mail_error.setVisibility(View.GONE);

            logMailUser();

        }
    }

    private void logMailUser() {
        lDialog.show();

        String email = log_in_mail.getEditableText().toString().trim();
        String password = log_in_pass.getEditableText().toString().trim();

        nAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        updatePass();
                    } else {
                        lDialog.dismiss();
                        Timber.tag("CREATING_USER_WITH_EMAIL: ").w(MessageFormat.format("FAILED: {0}", Objects.requireNonNull(task.getException()).getLocalizedMessage()));
                        auth_error.setText(task.getException().getLocalizedMessage());
                        auth_error.setVisibility(View.VISIBLE);
                        auth_error.requestFocus();
                    }

                });

    }

    private void updatePass() {

        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("account_details")
                .document("user_account_details")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    current_user = documentSnapshot.toObject(User.class);
                    assert this.current_user != null;
                    if (current_user.isUser_channel()){
                        lDialog.dismiss();
                        toPriestDash();
                    }else {
                        lDialog.dismiss();
                        toDashboard();
                    }

            });
    }

    private void toPriestDash() {
        Intent intent = new Intent(LogInActivity.this, PriestDashActivity.class);
        startActivity(intent);
        finish();
    }

    private void toDashboard() {
        Intent intent = new Intent(LogInActivity.this, UserDashActivity.class);
        startActivity(intent);
        finish();
    }
}