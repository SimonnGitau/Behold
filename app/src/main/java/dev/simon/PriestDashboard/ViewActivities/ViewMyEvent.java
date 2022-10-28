package dev.simon.PriestDashboard.ViewActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.PriestDashboard.EditActivities.EditEvent;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class ViewMyEvent extends AppCompatActivity {

    private ImageView eView_img;
    private TextView eView_time, eView_title, eView_venue, eView_count, eView_com;
    private MaterialButton eve_del, eve_edt;
    private SweetAlertDialog lDialog;
    private FirebaseFirestore db;

    private String eve_dp, eve_title, eve_des, eve_time, eve_date, eve_venue, eve_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_event);

        db = FirebaseFirestore.getInstance();

        eView_img = findViewById(R.id.eView_img);
        eView_time = findViewById(R.id.eView_time);
        eView_title = findViewById(R.id.eView_title);
        eView_venue = findViewById(R.id.eView_venue);
        eView_count = findViewById(R.id.eView_count);
        eve_del = findViewById(R.id.eve_del);
        eView_com = findViewById(R.id.eView_com);
        eve_edt = findViewById(R.id.eve_edt);


        Bundle bundle = getIntent().getExtras();
        eve_dp = bundle.getString("eve_dp");
        eve_title = bundle.getString("eve_title");
        eve_des = bundle.getString("eve_des");
        eve_time = bundle.getString("eve_time");
        eve_date = bundle.getString("eve_date");
        eve_venue = bundle.getString("eve_venue");
        eve_id = bundle.getString("eve_id");

        eView_title.setText(eve_title);
        eView_time.setText(eve_time);
        eView_venue.setText(eve_venue);
        Picasso.get().load(eve_dp).placeholder(R.drawable.loading).into(eView_img);

        eve_edt.setOnClickListener(View -> {
            Intent intent = new Intent(ViewMyEvent.this, EditEvent.class);
            Bundle bundle1 = new Bundle();
            bundle1.putString("eve_dp", eve_dp);
            bundle1.putString("eve_title", eve_title);
            bundle1.putString("eve_des", eve_des);
            bundle1.putString("eve_time", eve_time);
            bundle1.putString("eve_date", eve_date);
            bundle1.putString("eve_venue", eve_venue);
            bundle1.putString("eve_id", eve_id);
            intent.putExtras(bundle1);
            startActivity(intent);
        });

        eve_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lDialog = new SweetAlertDialog(ViewMyEvent.this, SweetAlertDialog.WARNING_TYPE);
                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                lDialog.setTitleText("You sure you wanna delete this event?");
                lDialog.setCancelable(true);
                lDialog.show();
                lDialog.setConfirmClickListener(View -> {
                    lDialog.dismiss();
                    deleteEve();
                });
                lDialog.setCancelClickListener(sweetAlertDialog -> lDialog.dismiss());

            }
        });
    }
    private void deleteEve() {
        db.collection("Events").document(eve_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        lDialog = new SweetAlertDialog(ViewMyEvent.this, SweetAlertDialog.SUCCESS_TYPE);
                        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        lDialog.setTitleText("Event removed!");
                        lDialog.setCancelable(true);
                        lDialog.show();
                        lDialog.setConfirmClickListener(View -> {
                            lDialog.dismiss();
                            startActivity(new Intent(ViewMyEvent.this, PriestDashActivity.class));
                            finish();
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        lDialog = new SweetAlertDialog(ViewMyEvent.this, SweetAlertDialog.ERROR_TYPE);
                        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        lDialog.setTitleText(e.getMessage().toString());
                        lDialog.setCancelable(true);
                        lDialog.show();
                    }
                });
    }

}