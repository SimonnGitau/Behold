package dev.simon.PriestDashboard.EditActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class EditEvent extends AppCompatActivity {

    private TextInputEditText edt_eve_title, edt_eve_venue, edt_eve_date, edt_eve_time, edt_eve_abt;
    private SweetAlertDialog lDialog;

    private String eve_id;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        db = FirebaseFirestore.getInstance();

        lDialog = new SweetAlertDialog(EditEvent.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);

        edt_eve_title = findViewById(R.id.edt_eve_title);
        edt_eve_venue = findViewById(R.id.edt_eve_venue);
        edt_eve_date = findViewById(R.id.edt_eve_date);
        edt_eve_time = findViewById(R.id.edt_eve_time);
        edt_eve_abt = findViewById(R.id.edt_eve_abt);
        MaterialButton edt_event = findViewById(R.id.edt_event);
        RoundedImageView edt_eve_flyer = findViewById(R.id.edt_eve_flyer);

        Bundle bundle = getIntent().getExtras();
        String eve_dp = bundle.getString("eve_dp");
        String eve_title = bundle.getString("eve_title");
        String eve_des = bundle.getString("eve_des");
        String eve_time = bundle.getString("eve_time");
        String eve_date = bundle.getString("eve_date");
        String eve_venue = bundle.getString("eve_venue");
        eve_id = bundle.getString("eve_id");

        edt_eve_title.setText(eve_title);
        edt_eve_venue.setText(eve_venue);
        edt_eve_date.setText(eve_date);
        edt_eve_time.setText(eve_time);
        edt_eve_abt.setText(eve_des);
        Picasso.get().load(eve_dp).placeholder(R.drawable.loading).into(edt_eve_flyer);

        edt_event.setOnClickListener(View -> updateTeach());

    }

    private void updateTeach() {
        lDialog.show();

        String eveTitle = Objects.requireNonNull(edt_eve_title.getText()).toString().trim();
        String eveVenue = Objects.requireNonNull(edt_eve_venue.getText()).toString().trim();
        String eveDate = Objects.requireNonNull(edt_eve_date.getText()).toString().trim();
        String eveTime = Objects.requireNonNull(edt_eve_time.getText()).toString().trim();
        String eveAbout = Objects.requireNonNull(edt_eve_abt.getText()).toString().trim();

        db.collection("Events")
                .document(eve_id)
                .update("event_date", eveDate,
                        "event_venue", eveVenue,
                        "event_date", eveDate,
                        "event_time", eveTime,
                        "event_des", eveAbout,
                        "event_title", eveTitle)
                .addOnSuccessListener(unused -> {
                    lDialog.dismiss();
                    lDialog = new SweetAlertDialog(EditEvent.this, SweetAlertDialog.SUCCESS_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText("Event updated successfully!");
                    lDialog.setCancelable(true);
                    lDialog.show();
                    lDialog.setConfirmClickListener(View ->{
                        lDialog.dismiss();
                        startActivity(new Intent(EditEvent.this, PriestDashActivity.class));
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    lDialog.dismiss();
                    lDialog = new SweetAlertDialog(EditEvent.this, SweetAlertDialog.ERROR_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText(e.getLocalizedMessage());
                    lDialog.setCancelable(true);
                    lDialog.show();
                });

}
}