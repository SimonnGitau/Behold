package dev.simon.PriestDashboard.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

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
import dev.simon.Classes.User;
import dev.simon.PriestDashboard.EditActivities.EditEvent;
import dev.simon.PriestDashboard.Fragments.MyEventsFragment;
import dev.simon.PriestDashboard.PriestDashActivity;
import dev.simon.behold.R;

public class AddEventActivity extends AppCompatActivity {

    private TextInputEditText eve_title, eve_venue, eve_date, eve_time, eve_abt;
    private RoundedImageView eve_flyer, eve_add;
    private MaterialButton event_add;

    private ActivityResultLauncher<Intent> launcher;
    private SweetAlertDialog lDialog;
    private FirebaseFirestore db;
    Event new_event;

    private Uri imgUri;
    User current_user;

    KeyGenerator key_gen = new KeyGenerator(36);
    String new_event_id = key_gen.nextString(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        db = FirebaseFirestore.getInstance();

        eve_title= findViewById(R.id.eve_title);
        eve_venue= findViewById(R.id.eve_venue);
        eve_date= findViewById(R.id.eve_date);
        eve_time= findViewById(R.id.eve_time);
        eve_flyer= findViewById(R.id.eve_flyer);
        eve_add= findViewById(R.id.eve_add);
        event_add= findViewById(R.id.event_add);
        eve_abt = findViewById(R.id.eve_abt);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imgUri = result.getData().getData();
                Picasso.get().load(imgUri).into(eve_flyer);
                eve_flyer.setVisibility(View.VISIBLE);

            }
        });


        eve_add.setOnClickListener(View ->{
            openGallery();
        });

        event_add.setOnClickListener(View ->{
            checkFields();
        });

    }

    private void checkFields() {

        if (eve_flyer.getDrawable() == null ||
                Objects.requireNonNull(eve_title.getText()).toString().equals("") ||
                Objects.requireNonNull(eve_abt.getText()).toString().equals("") ||
                Objects.requireNonNull(eve_time.getText()).toString().equals("") ||
                Objects.requireNonNull(eve_date.getText()).toString().equals("") ||
                Objects.requireNonNull(eve_venue.getText()).toString().equals("")){
            lDialog = new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.WARNING_TYPE);
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
            updateEventFlyer(imgUri);
        }

    }

    private void updateEventFlyer(Uri imgUri) {

        lDialog = new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);
        lDialog.show();

        String eventName = Objects.requireNonNull(eve_title.getText()).toString().trim();
        String eventTime = Objects.requireNonNull(eve_time.getText()).toString().trim();
        String eventDate = Objects.requireNonNull(eve_date.getText()).toString().trim();
        String eventDes = Objects.requireNonNull(eve_abt.getText()).toString().trim();
        String eventVenue = Objects.requireNonNull(eve_venue.getText()).toString().trim();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Events")
                .child(new_event_id);

        final StorageReference img_name = storageReference
                .child("event_img" + System.currentTimeMillis());

        img_name.putFile(imgUri).addOnSuccessListener(taskSnapshot ->
                img_name.getDownloadUrl().addOnSuccessListener(uri -> {
                    Toast.makeText(AddEventActivity.this, "Event Image updated success!",
                            Toast.LENGTH_LONG).show();

                    new_event = new Event();
                    new_event.setEvent_date(eventDate);
                    new_event.setEvent_time(eventTime);
                    new_event.setEvent_title(eventName);
                    new_event.setEvent_des(eventDes);
                    new_event.setEvent_venue(eventVenue);
                    new_event.setEvent_img_uri(uri.toString());
                    new_event.setEvent_id(new_event_id);

                    db.collection("Events")
                            .document(new_event_id)
                            .set(new_event)
                            .addOnSuccessListener(unused -> {
                                lDialog.dismiss();
                                lDialog = new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                                lDialog.setTitleText("Event added successfully!");
                                lDialog.setCancelable(true);
                                lDialog.show();
                                lDialog.setConfirmClickListener(View ->{
                                    lDialog.dismiss();
                                    startActivity(new Intent(AddEventActivity.this, PriestDashActivity.class));
                                    finish();
                                });
                            })
                            .addOnFailureListener(e -> {
                                lDialog.dismiss();
                                lDialog = new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.ERROR_TYPE);
                                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                                lDialog.setTitleText(e.getLocalizedMessage());
                                lDialog.setCancelable(true);
                                lDialog.show();

                            });
                }).addOnFailureListener(e -> {
                    lDialog.dismiss();
                    lDialog = new SweetAlertDialog(AddEventActivity.this, SweetAlertDialog.ERROR_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText(e.getLocalizedMessage());
                    lDialog.setCancelable(true);
                    lDialog.show();
                }));

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);

    }
}