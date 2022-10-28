package dev.simon.behold;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Authenticate.LogInActivity;
import dev.simon.Authenticate.SignUpActivity;
import dev.simon.Authenticate.UpdateProfile;
import dev.simon.Classes.User;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.UserDashboard.UserDashActivity;
import timber.log.Timber;

public class AuthActivity extends AppCompatActivity {

    private SweetAlertDialog pDialog;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;

    private User user;
    private FirebaseFirestore db;
    private User current_user;

    private boolean newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        db = FirebaseFirestore.getInstance();

        MaterialButton sign_up = findViewById(R.id.sign_up);
        MaterialButton log_in = findViewById(R.id.log_in);

        sign_up.setOnClickListener(View -> checkStatus(true));

        log_in.setOnClickListener(View -> checkStatus(false));
    }

    private void checkStatus(boolean asNew) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AuthActivity.this,
                R.style.BottomSheetDialogTheme);

        android.view.View bottomSheetView = LayoutInflater.from(AuthActivity.this)
                .inflate(R.layout.layout_bottom_sheet,
                        findViewById(R.id.bottom_sheet_container));

        bottomSheetView.findViewById(R.id.google_auth).setOnClickListener(view -> {

            if (asNew){
                newUser = true;
                google_continue();
            }else{
                newUser = false;
                google_continue();
            }

            bottomSheetDialog.dismiss();

        });

        bottomSheetView.findViewById(R.id.mail_auth).setOnClickListener(view -> {
            if (asNew){
                startActivity(new Intent(AuthActivity.this, SignUpActivity.class));
            }else{
                startActivity(new Intent(AuthActivity.this, LogInActivity.class));
            }
            bottomSheetDialog.dismiss();

        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    private void google_continue() {


        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(false)
                .build();

        signInGoogleClient();

    }

    private void signInGoogleClient() {

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Timber.tag(TAG).e("FAILED_TO_START_ONE_TAP_UI: %s", e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Timber.tag(TAG).d(e.getLocalizedMessage());
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mAuth = FirebaseAuth.getInstance();

        if (newUser){
            if (requestCode == REQ_ONE_TAP) {
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        SignInCredential googleCredential = null;
                        try {
                            googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
                        } catch (ApiException e) {
                            pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                            pDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                            pDialog.setCancelable(true);
                            pDialog.show();
                            e.printStackTrace();
                        }
                        assert googleCredential != null;
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);

                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        assert firebaseUser != null;
                                        Timber.tag("CREATING_USER_WITH_GOOGLE: ").w("SUCCESS!");
                                        user = new User();
                                        user.setUser_id(firebaseUser.getUid());
                                        user.setUser_mail(firebaseUser.getEmail());
                                        user.setUser_channel(false);

                                        updateUserDB();

                                    } else {
                                        // If sign in fails, display a message to the user.

                                        pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                                        pDialog.setTitleText(Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                        pDialog.setCancelable(true);
                                        pDialog.show();

                                        Timber.tag("CREATING_USER_WITH_GOOGLE:: ").w("FAILED: %s", Objects.requireNonNull(task.getException()).getLocalizedMessage());
                                    }

                                });
                    }
                } catch (ApiException e) {
                    // ...
                    pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    pDialog.setTitleText(e.getLocalizedMessage());
                    pDialog.setCancelable(true);
                    pDialog.show();
                    Timber.tag("ACTIVITY_RESULT_FAILED:: ").w(e.getLocalizedMessage());
                }
            }
        }else{
            if (requestCode == REQ_ONE_TAP) {
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    String tokenID = credential.getId();
                    if (idToken != null) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null) {
                            String mail = (user).getEmail();
                            if (tokenID.equals(mail)) {

                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .collection("account_details")
                                        .document("user_account_details")
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            current_user = documentSnapshot.toObject(User.class);
                                            assert this.current_user != null;
                                            if (current_user.isUser_channel()){
                                                toPriestDash();
                                            }else {
                                                toDashboard();
                                            }

                                        });

                            } else {

                                pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                                pDialog.setTitleText(R.string.log_error);
                                pDialog.setCancelable(true);
                                pDialog.show();
                            }
                        }else{

                            pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                            pDialog.setTitleText(R.string.log_error);
                            pDialog.setCancelable(false);
                            pDialog.show();
                        }

                    } else {
                        Toast.makeText(AuthActivity.this, "An error occurred! Try again.", Toast.LENGTH_SHORT).show();
                    }

                } catch (ApiException e) {
                    e.printStackTrace();
                    Timber.tag("ACTIVITY_RESULT_ERROR: ").d(e.getLocalizedMessage());
                    Toast.makeText(AuthActivity.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void updateUserDB() {
        Timber.tag("USER_UPDATE_DB: ").w("STARTING...");

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        pDialog.setTitleText("Updating user..");
        pDialog.setCancelable(false);
        pDialog.show();

        // Add a new document with a generated ID
        db.collection("Users")
                .document(user.getUser_id())
                .collection("account_details")
                .document("user_account_details")
                .set(user)
                .addOnSuccessListener(unused -> {
                    pDialog.dismiss();
                    Timber.tag("USER_UPDATE_DB: ").w("SUCCESSFUL!");
                    Toast.makeText(AuthActivity.this, "Account created!",
                            Toast.LENGTH_SHORT).show();
                    updateAccount();
                })
                .addOnFailureListener(e -> {
                    Timber.tag("USER_UPDATE_DB: ").w("FAILED: %s", e.getLocalizedMessage());
                    pDialog.dismiss();

                    pDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    pDialog.setTitleText(e.getLocalizedMessage());
                    pDialog.setCancelable(true);
                    pDialog.show();
                });
    }

    private void updateAccount() {
        startActivity(new Intent(AuthActivity.this, UpdateProfile.class));
        finish();
    }

    private void toDashboard() {
        startActivity(new Intent(AuthActivity.this, UserDashActivity.class));
        finish();
    }

    private void toPriestDash() {
        startActivity(new Intent(AuthActivity.this, PriestDashActivity.class));
        finish();
    }
}