package dev.simon.PriestDashboard.EditActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.User;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class EditTea extends AppCompatActivity {

    private TextInputEditText edt_tea_topic, edt_tea_sub, edt_tea_verses, edt_tea_det;
    private SweetAlertDialog lDialog;

    String tea_id;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tea);

        db = FirebaseFirestore.getInstance();

        lDialog = new SweetAlertDialog(EditTea.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);

        Bundle bundle = getIntent().getExtras();
        String tea_dp = bundle.getString("tea_dp");
        String tea_title = bundle.getString("tea_title");
        String tea_des = bundle.getString("tea_des");
        String tea_sub = bundle.getString("tea_sub");
        String tea_verse = bundle.getString("tea_verse");
        tea_id = bundle.getString("tea_id");

        edt_tea_topic = findViewById(R.id.edt_tea_topic);
        edt_tea_sub = findViewById(R.id.edt_tea_sub);
        edt_tea_verses = findViewById(R.id.edt_tea_verses);
        edt_tea_det = findViewById(R.id.edt_tea_det);
        ImageView edt_teach_bg = findViewById(R.id.edt_teach_bg);
        MaterialButton edt_tea = findViewById(R.id.edt_tea);

        edt_tea_topic.setText(tea_title);
        edt_tea_sub.setText(tea_sub);
        edt_tea_verses.setText(tea_verse);
        edt_tea_det.setText(tea_des);
        Picasso.get().load(tea_dp).placeholder(R.drawable.loading).into(edt_teach_bg);


        edt_tea.setOnClickListener(View -> updateTeach());

    }

    private void updateTeach() {
        lDialog.show();

        String teaTopic = Objects.requireNonNull(edt_tea_topic.getText()).toString().trim();
        String teaSub = Objects.requireNonNull(edt_tea_sub.getText()).toString().trim();
        String teaDes = Objects.requireNonNull(edt_tea_det.getText()).toString().trim();
        String teaVerses = Objects.requireNonNull(edt_tea_verses.getText()).toString().trim();

        db.collection("Teachings")
                .document(tea_id)
                .update("teach_des", teaDes,
                        "teach_sub", teaSub,
                        "teach_topic", teaTopic,
                        "teach_verses", teaVerses)
                .addOnSuccessListener(unused -> {
                    lDialog.dismiss();
                    lDialog = new SweetAlertDialog(EditTea.this, SweetAlertDialog.SUCCESS_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText("Teaching updated successfully!");
                    lDialog.setCancelable(true);
                    lDialog.show();
                    lDialog.setConfirmClickListener(View->{
                        lDialog.dismiss();
                        startActivity(new Intent(EditTea.this, PriestDashActivity.class));
                        finish();
                    });

                })
                .addOnFailureListener(e -> {
                    lDialog.dismiss();
                    lDialog = new SweetAlertDialog(EditTea.this, SweetAlertDialog.ERROR_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText(e.getLocalizedMessage());
                    lDialog.setCancelable(true);
                    lDialog.show();
                });
    }
}