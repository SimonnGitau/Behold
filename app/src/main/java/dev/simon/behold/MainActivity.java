package dev.simon.behold;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import dev.simon.Classes.User;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.UserDashboard.UserDashActivity;

public class MainActivity extends AppCompatActivity {

    private User current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FirebaseUser currentUser;
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(currentUser != null){
                FirebaseFirestore.getInstance().collection("Users")
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .collection("account_details")
                        .document("user_account_details")
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            current_user = documentSnapshot.toObject(User.class);
                            assert current_user != null;
                            if (current_user.isUser_channel()){
                                toPriestDash();
                            }else {
                                toDashboard();
                            }
                        });
            }else {
                toAuthAct();
            }
        }, 3000);
    }

    private void toDashboard() {
        Intent intent = new Intent(MainActivity.this, UserDashActivity.class);
        startActivity(intent);
        finish();
    }

    private void toPriestDash() {
        Intent intent = new Intent(MainActivity.this, PriestDashActivity.class);
        startActivity(intent);
        finish();
    }

    private void toAuthAct() {
        Intent intent = new Intent(MainActivity.this, PrivacyTerms.class);
        startActivity(intent);
        finish();
    }
}