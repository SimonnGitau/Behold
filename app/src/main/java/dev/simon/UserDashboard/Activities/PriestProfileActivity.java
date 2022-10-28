package dev.simon.UserDashboard.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import dev.simon.Classes.Channel;
import dev.simon.Classes.User;
import dev.simon.UserDashboard.Payments.DonateActivity;
import dev.simon.behold.R;

public class PriestProfileActivity extends AppCompatActivity {

    private CircleImageView don_dp;
    private TextView don_name, don_church, don_church_loc, don_num;
    private MaterialButton don_donate;
    private SweetAlertDialog lDialog;

    Channel curr_channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priest_profile);

        Bundle bundle = getIntent().getExtras();
        String nn = bundle.getString("priest_id");
        Toast.makeText(this, nn, Toast.LENGTH_SHORT).show();

        don_dp = findViewById(R.id.don_dp);
        don_name = findViewById(R.id.don_name);
        don_church = findViewById(R.id.don_church);
        don_church_loc = findViewById(R.id.don_church_loc);
        don_num = findViewById(R.id.don_num);
        don_donate = findViewById(R.id.don_donate);


        FirebaseFirestore.getInstance().collection("Channels")
                .document(nn)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        curr_channel = documentSnapshot.toObject(Channel.class);
                        assert curr_channel != null;

                        Picasso.get().load(curr_channel.getChan_dp_url()).placeholder(R.drawable.loading).into(don_dp);
                        don_name.setText(curr_channel.getChan_name());
                        don_church.setText(curr_channel.getChurch_name());
                        don_church_loc.setText(curr_channel.getChurch_loc());

                        don_donate.setOnClickListener(View->{
                            Intent intent = new Intent(PriestProfileActivity.this, DonateActivity.class);
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("phone_number", String.valueOf(curr_channel.getChan_mpesa_num()));
                            intent.putExtras(bundle1);
                            startActivity(intent);
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PriestProfileActivity.this, "Failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}