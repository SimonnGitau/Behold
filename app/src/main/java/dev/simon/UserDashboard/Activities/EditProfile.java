package dev.simon.UserDashboard.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import dev.simon.Authenticate.Classes.GenderAdapter;
import dev.simon.Authenticate.Classes.GenderItem;
import dev.simon.Classes.User;
import dev.simon.UserDashboard.UserDashActivity;
import dev.simon.behold.R;
import timber.log.Timber;

public class EditProfile extends AppCompatActivity {

    private CircleImageView edit_dp;
    private TextInputEditText edit_username, edt_phone;
    private Spinner edit_Spinner;
    private MaterialButton make_updates;
    private TextView edt_dp_error, edt_name_error, edt_gender_error, phone_error;
    private CountryCodePicker code_picker;

    GenderItem clickedItem;
    private ActivityResultLauncher<Intent> launcher;
    private SweetAlertDialog lDialog;

    private ArrayList<GenderItem> mGenderList;
    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        lDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading..");
        lDialog.setCancelable(false);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                image_uri = result.getData().getData();
                Timber.tag("LOADING_USER_DP:: ").w("SUCCESS");
                Picasso.get().load(image_uri).into(edit_dp);
            }
        });

        edit_dp = findViewById(R.id.edit_dp);
        edit_username = findViewById(R.id.edit_username);
        edit_Spinner = findViewById(R.id.edit_Spinner);
        edt_phone = findViewById(R.id.edt_phone);
        make_updates = findViewById(R.id.make_updates);
        edt_dp_error = findViewById(R.id.edt_dp_error);
        edt_name_error = findViewById(R.id.edt_name_error);
        edt_gender_error = findViewById(R.id.edt_gender_error);
        phone_error = findViewById(R.id.phone_error);
        code_picker = findViewById(R.id.code_picker);

        edit_dp.setOnClickListener(View -> openGallery());

        initList();
        listeners();

        updateFields();

        make_updates.setOnClickListener(View -> checkFields());

        GenderAdapter mAdapter = new GenderAdapter(this, mGenderList);
        edit_Spinner.setAdapter(mAdapter);

        edit_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clickedItem = (GenderItem) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                edt_gender_error.setVisibility(View.VISIBLE);
                edt_gender_error.requestFocus();
            }
        });

    }

    private void updateFields() {
        lDialog.show();

        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("account_details")
                .document("user_account_details")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User current_user = documentSnapshot.toObject(User.class);
                    assert current_user != null;
                    lDialog.dismiss();
                    Picasso.get().load(current_user.getUser_dp_url()).placeholder(R.drawable.loading).into(edit_dp);
                    edit_username.setText(current_user.getUser_name());

                    if (current_user.getUser_gender().equals("MALE")){
                        edit_Spinner.setSelection(1);
                    }else{
                        edit_Spinner.setSelection(2);
                    }

                    image_uri = Uri.parse(current_user.getUser_dp_url());

                    edt_phone.setText(String.valueOf(current_user.getUser_phone()));


                });
    }

    private void checkFields() {

        if (edit_dp.getDrawable() == null){
            edt_dp_error.setVisibility(View.VISIBLE);
            edt_dp_error.requestFocus();
        }else if (edit_username.getText().toString().trim().equals("") || edit_username.getText().toString().trim().length() < 5){
            edt_name_error.setVisibility(View.VISIBLE);
            edt_name_error.requestFocus();
        }else if (edit_Spinner.getSelectedItemId() == 0){
            edt_gender_error.setVisibility(View.VISIBLE);
            edt_gender_error.requestFocus();
        }else if (edt_phone.getText().toString().trim().equals("") || edt_phone.getText().toString().trim().length() < 8){
            phone_error.setText("Please enter a valid phone number!");
            phone_error.setVisibility(View.VISIBLE);
            phone_error.requestFocus();
        }else{
            edt_dp_error.setVisibility(View.GONE);
            edt_name_error.setVisibility(View.GONE);
            edt_gender_error.setVisibility(View.GONE);
            phone_error.setVisibility(View.GONE);

            changeFields(image_uri);

        }

    }

    private void changeFields(Uri image_uri) {

        lDialog.show();

        Timber.tag("UPDATING_USER_DP:: ").w("....STARTED....");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        final StorageReference img_name = storageReference
                .child("dp_img" + System.currentTimeMillis());

        img_name.putFile(image_uri).addOnSuccessListener(taskSnapshot ->
                img_name.getDownloadUrl().addOnSuccessListener(uri -> {
                    Toast.makeText(EditProfile.this, "DP updated success!",
                            Toast.LENGTH_LONG).show();

                    FirebaseFirestore.getInstance().collection("Users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("account_details")
                            .document("user_account_details")
                            .update("user_country", code_picker.getSelectedCountryName(),
                                    "user_gender", clickedItem,
                                    "user_phone", String.valueOf(edt_phone.getText()),
                                    "user_name", edit_username.getText(),
                                    "user_dp_url", uri.toString())
                            .addOnSuccessListener(unused -> {
                                lDialog.dismiss();
                                lDialog = new SweetAlertDialog(EditProfile.this, SweetAlertDialog.SUCCESS_TYPE);
                                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                                lDialog.setTitleText("Profile updated!");
                                lDialog.setCancelable(true);
                                lDialog.show();
                                lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        startActivity(new Intent(EditProfile.this, UserDashActivity.class));
                                        finish();
                                    }
                                });

                            }).addOnFailureListener(e -> {
                                lDialog.dismiss();
                                Timber.tag("CREATING_USER_WITH_EMAIL: ").w(MessageFormat.format("FAILED: {0}", Objects.requireNonNull(e.getLocalizedMessage())));
                                lDialog = new SweetAlertDialog(EditProfile.this, SweetAlertDialog.ERROR_TYPE);
                                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                                lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                                lDialog.setCancelable(true);
                                lDialog.show();

                            });

                }).addOnFailureListener(e -> {
                    lDialog.dismiss();
                    Timber.tag("CREATING_USER_WITH_EMAIL: ").w(MessageFormat.format("FAILED: {0}", Objects.requireNonNull(e.getLocalizedMessage())));
                    lDialog = new SweetAlertDialog(EditProfile.this, SweetAlertDialog.ERROR_TYPE);
                    lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                    lDialog.setTitleText(Objects.requireNonNull(e.getLocalizedMessage()));
                    lDialog.setCancelable(true);
                    lDialog.show();
                }));

    }

    private void listeners() {
        code_picker.setOnCountryChangeListener(() -> edt_phone.setText(code_picker.getSelectedCountryCodeWithPlus()));
    }

    private void initList() {
        mGenderList = new ArrayList<>();
        mGenderList.add(new GenderItem("--SELECT GENDER--", R.drawable.account));
        mGenderList.add(new GenderItem("MALE", R.drawable.account));
        mGenderList.add(new GenderItem("FEMALE", R.drawable.account));
    }

    private void openGallery() {
        Timber.tag("LOADING_USER_DP:: ").w("OPENING GALLERY");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);

    }
}