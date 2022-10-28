package dev.simon.PriestDashboard.ViewActivities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.PriestDashboard.EditActivities.EditTea;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class ViewMyTeaching extends AppCompatActivity {

    private ImageView my_tea_view;
    private TextView my_tea_title, my_tea_sub, my_tea_verse, my_tea_con;
    private MaterialButton remove_tea, edit_tea;
    private SweetAlertDialog lDialog;
    private String tea_dp, tea_title, tea_des, tea_sub, tea_verse, tea_id;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_teaching);

        db = FirebaseFirestore.getInstance();

        my_tea_view = findViewById(R.id.my_tea_view);
        my_tea_title = findViewById(R.id.my_tea_title);
        my_tea_sub = findViewById(R.id.my_tea_sub);
        my_tea_verse = findViewById(R.id.my_tea_verse);
        my_tea_con = findViewById(R.id.my_tea_con);
        remove_tea = findViewById(R.id.remove_tea);
        edit_tea = findViewById(R.id.edit_tea);

        Bundle bundle = getIntent().getExtras();
        tea_dp = bundle.getString("tea_dp");
        tea_title = bundle.getString("tea_title");
        tea_des = bundle.getString("tea_des");
        tea_sub = bundle.getString("tea_sub");
        tea_verse = bundle.getString("tea_verse");
        tea_id = bundle.getString("tea_id");

        my_tea_con.setText(tea_des);
        my_tea_sub.setText(tea_sub);
        my_tea_title.setText(tea_title);
        my_tea_verse.setText(tea_verse);
        Picasso.get().load(tea_dp).placeholder(R.drawable.loading).into(my_tea_view);

        edit_tea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTea();
            }
        });

        remove_tea.setOnClickListener(View ->{

            lDialog = new SweetAlertDialog(ViewMyTeaching.this, SweetAlertDialog.WARNING_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("You sure you wanna delete the document? This action is irreversible.");
            lDialog.setCancelable(true);
            lDialog.show();
            lDialog.setCancelClickListener(sweetAlertDialog -> lDialog.dismiss());
            lDialog.setConfirmClickListener(sweetAlertDialog -> {
                lDialog.dismiss();
                deleteTea();
            });

        });

    }

    private void deleteTea() {

        db.collection("Teachings").document(tea_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        lDialog = new SweetAlertDialog(ViewMyTeaching.this, SweetAlertDialog.SUCCESS_TYPE);
                        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        lDialog.setTitleText("Teaching removed!");
                        lDialog.setCancelable(true);
                        lDialog.show();

                        lDialog.setConfirmClickListener(View ->{
                            startActivity(new Intent(ViewMyTeaching.this, PriestDashActivity.class));
                            finish();
                        });

                    }
                }).addOnFailureListener(e -> {
                    lDialog = new SweetAlertDialog(ViewMyTeaching.this, SweetAlertDialog.ERROR_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText(e.getMessage().toString());
                    lDialog.setCancelable(true);
                    lDialog.show();
                });

    }

    private void editTea() {
        Intent intent = new Intent(ViewMyTeaching.this, EditTea.class);
        Bundle bundle = new Bundle();
        bundle.putString("tea_dp", tea_dp);
        bundle.putString("tea_title", tea_title);
        bundle.putString("tea_des", tea_des);
        bundle.putString("tea_verse", tea_verse);
        bundle.putString("tea_sub", tea_sub);
        bundle.putString("tea_id", tea_id);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}