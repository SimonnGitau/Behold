
package dev.simon.PriestDashboard.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Event;
import dev.simon.Classes.KeyGenerator;
import dev.simon.Classes.Teach;
import dev.simon.Classes.User;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;
import timber.log.Timber;

public class AddTeachingActivity extends AppCompatActivity {

    private MaterialButton tea_add;
    private TextInputEditText tea_topic, tea_sub, tea_verses, tea_det;
    private RoundedImageView teach_bg, teach_add;
    private ActivityResultLauncher<Intent> launcher;
    Uri image_uri;

    private SweetAlertDialog lDialog;
    private FirebaseFirestore db;
    Teach new_teach;
    User current_user;

    KeyGenerator key_gen = new KeyGenerator(36);
    String new_teach_id = key_gen.nextString(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teaching);

        db = FirebaseFirestore.getInstance();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                image_uri = result.getData().getData();
                Picasso.get().load(image_uri).into(teach_bg);
            }
        });

        tea_add = findViewById(R.id.tea_add);
        tea_topic = findViewById(R.id.tea_topic);
        tea_sub = findViewById(R.id.tea_sub);
        tea_verses = findViewById(R.id.tea_verses);
        tea_det = findViewById(R.id.tea_det);
        tea_verses = findViewById(R.id.tea_verses);
        teach_bg = findViewById(R.id.teach_bg);
        teach_add = findViewById(R.id.teach_add);

        teach_add.setOnClickListener(View->{
            openGallery();
        });

        tea_add.setOnClickListener(View ->{
            checkFields();
        });

    }

    private void checkFields() {

        if (teach_bg.getDrawable() == null ||
                Objects.requireNonNull(tea_topic.getText()).toString().trim().equals("") ||
                Objects.requireNonNull(tea_sub.getText()).toString().trim().equals("") ||
                Objects.requireNonNull(tea_verses.getText()).toString().trim().equals("") ||
                Objects.requireNonNull(tea_det.getText()).toString().trim().equals("") ){
            lDialog = new SweetAlertDialog(AddTeachingActivity.this, SweetAlertDialog.WARNING_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("All fields required!");
            lDialog.setCancelable(false);
            lDialog.show();
            lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    lDialog.dismiss();
                }
            });
        }else {
            updateTeachingDP(image_uri);
        }

    }

    private void updateTeachingDP(Uri image_uri) {

        lDialog = new SweetAlertDialog(AddTeachingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);
        lDialog.show();

        String teaTopic = Objects.requireNonNull(tea_topic.getText()).toString().trim();
        String teaSub = Objects.requireNonNull(tea_sub.getText()).toString().trim();
        String teaDes = Objects.requireNonNull(tea_det.getText()).toString().trim();
        String teaVerses = Objects.requireNonNull(tea_verses.getText()).toString().trim();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Teachings")
                .child(new_teach_id);

        final StorageReference img_name = storageReference
                .child("teach_img" + System.currentTimeMillis());

        img_name.putFile(image_uri).addOnSuccessListener(taskSnapshot ->
                img_name.getDownloadUrl().addOnSuccessListener(uri -> {

                    new_teach = new Teach();
                    new_teach.setTeach_topic(teaTopic);
                    new_teach.setTeach_des(teaDes);
                    new_teach.setTeach_sub(teaSub);
                    new_teach.setTeach_verses(teaVerses);
                    new_teach.setTeach_img(uri.toString());
                    new_teach.setTeach_id(new_teach_id);

                    db.collection("Teachings")
                            .document(new_teach_id)
                            .set(new_teach)
                            .addOnSuccessListener(unused -> {
                                lDialog.dismiss();

                                lDialog = new SweetAlertDialog(AddTeachingActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                                lDialog.setTitleText("Teaching updated!");
                                lDialog.setCancelable(true);
                                lDialog.show();
                                lDialog.setConfirmClickListener(View ->{
                                    lDialog.dismiss();
                                    startActivity(new Intent(AddTeachingActivity.this, PriestDashActivity.class));
                                    finish();
                                });



                            })
                            .addOnFailureListener(e -> {
                                lDialog.dismiss();
                                lDialog = new SweetAlertDialog(AddTeachingActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                                lDialog.setTitleText(e.getLocalizedMessage());
                                lDialog.setCancelable(true);
                                lDialog.setConfirmClickListener(View ->{
                                    lDialog.dismiss();
                                });

                            });
                }).addOnFailureListener(e -> {
                    lDialog.dismiss();
                    lDialog = new SweetAlertDialog(AddTeachingActivity.this, SweetAlertDialog.ERROR_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText(e.getLocalizedMessage());
                    lDialog.setCancelable(true);
                    lDialog.setConfirmClickListener(View ->{
                        lDialog.dismiss();
                    });
                }));

    }



    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }
}