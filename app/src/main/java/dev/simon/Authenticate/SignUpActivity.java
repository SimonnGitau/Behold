package dev.simon.Authenticate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.MessageFormat;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.User;
import dev.simon.behold.R;
import timber.log.Timber;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText sign_up_mail, sign_up_pass, sign_con_pass;
    private TextView pass1_error, pass_error, mail_error;

    private SweetAlertDialog pDialog;
    private FirebaseAuth mAuth;

    private User user;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        pDialog.setTitleText("Authenticating...");
        pDialog.setCancelable(false);

        db = FirebaseFirestore.getInstance();

        MaterialButton auth_btn = findViewById(R.id.auth_btn);

        sign_up_mail = findViewById(R.id.sign_up_mail);
        sign_up_pass = findViewById(R.id.sign_up_pass);
        sign_con_pass = findViewById(R.id.sign_con_pass);
        mail_error = findViewById(R.id.mail_error);
        pass_error = findViewById(R.id.pass_error);
        pass1_error = findViewById(R.id.pass1_error);

        auth_btn.setOnClickListener(View -> checkFields());

    }

    private void checkFields() {

        if (Objects.requireNonNull(sign_up_pass.getText()).toString().trim().equals("")
                || Objects.requireNonNull(sign_up_pass.getText()).toString().trim().length() < 6) {
            pass_error.setVisibility(View.VISIBLE);
            pass_error.requestFocus();
        }else if (Objects.requireNonNull(sign_up_mail.getText()).toString().trim().equals("")){
            mail_error.setVisibility(View.VISIBLE);
            mail_error.requestFocus();
        }else if (!sign_up_pass.getText().toString().trim().equals(Objects.requireNonNull(sign_con_pass.getText()).toString().trim())){
            pass1_error.setVisibility(View.VISIBLE);
            pass1_error.requestFocus();
        }else {

            pass1_error.setVisibility(View.GONE);
            pass_error.setVisibility(View.GONE);
            mail_error.setVisibility(View.GONE);

            signAsUser();
        }

    }

    private void signAsUser() {
        pDialog.show();

        Timber.tag("STARTING_USER_MAIL_UPDATE:").w(".........");

        String email = sign_up_mail.getEditableText().toString().trim();
        String password = sign_up_pass.getEditableText().toString().trim();

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Timber.tag("CREATING_USER_WITH_EMAIL: ").w("SUCCESSFUL!");
                        FirebaseUser mailUser = mAuth.getCurrentUser();
                        assert mailUser != null;

                        user = new User();
                        user.setUser_pass(password);
                        user.setUser_id(mailUser.getUid());
                        user.setUser_mail(mailUser.getEmail());
                        user.setUser_channel(false);

                        updateUserDB();

                    } else {
                        pDialog.dismiss();
                        Timber.tag("CREATING_USER_WITH_EMAIL: ").w(MessageFormat.format("FAILED: {0}", Objects.requireNonNull(task.getException()).getLocalizedMessage()));
                        pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        pDialog.setTitleText(Objects.requireNonNull(task.getException().getLocalizedMessage()));
                        pDialog.setCancelable(true);
                        pDialog.show();
                    }
                });
    }

    private void updateUserDB(){
        Timber.tag("USER_UPDATE_DB: ").w("STARTING...");

        // Add a new document with a generated ID
        db.collection("Users")
                .document(user.getUser_id())
                .collection("account_details")
                .document("user_account_details")
                .set(user)
                .addOnSuccessListener(unused -> {

                    pDialog.dismiss();
                    Timber.tag("USER_UPDATE_DB: ").w("SUCCESSFUL!");
                    Toast.makeText(SignUpActivity.this, "Account created!",
                            Toast.LENGTH_SHORT).show();
                    toLogIn();
                })
                .addOnFailureListener(e -> {
                    Timber.tag("USER_UPDATE_DB: ").w("FAILED: %s", e.getLocalizedMessage());
                    Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();

                    pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    pDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                    pDialog.setCancelable(true);
                    pDialog.show();

                    pDialog.dismiss();

                });
    }

    private void toLogIn() {
        Intent intent = new Intent(SignUpActivity.this, UpdateProfile.class);
        startActivity(intent);
        finish();
    }

}