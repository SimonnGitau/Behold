package dev.simon.Authenticate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Authenticate.Classes.GenderAdapter;
import dev.simon.Authenticate.Classes.GenderItem;
import dev.simon.behold.R;

public class UpdateProfile extends AppCompatActivity {

    private Spinner customSpinner;
    private TextInputEditText phone_input, username;
    private ArrayList<GenderItem> mGenderList;
    private CountryCodePicker ccp;
    private TextView name_error;
    private TextView gender_error;
    private TextView phone_error;
    String userPhone;

    GenderItem genderItem;

    private SweetAlertDialog lDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        SweetAlertDialog lDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Finishing up...");
        lDialog.setCancelable(false);



        customSpinner = findViewById(R.id.custom_spinner);
        ccp = findViewById(R.id.ccp);
        phone_input = findViewById(R.id.phone_input);
        username = findViewById(R.id.username);
        MaterialButton update_profile = findViewById(R.id.update_profile);

        //Error texts
        phone_error = findViewById(R.id.phone_error);
        gender_error = findViewById(R.id.gender_error);
        name_error = findViewById(R.id.name_error);
        TextView dp_error = findViewById(R.id.dp_error);

        initList();
        listeners();

        update_profile.setOnClickListener(View -> checkFields());

        GenderAdapter mAdapter = new GenderAdapter(this, mGenderList);
        customSpinner.setAdapter(mAdapter);

        customSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderItem = (GenderItem) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender_error.setVisibility(View.VISIBLE);
                gender_error.requestFocus();
            }
        });

    }

    private void checkFields() {
        if (Objects.requireNonNull(username.getText()).toString().trim().equals("") || Objects.requireNonNull(username.getText()).toString().trim().length() < 5){
            name_error.setVisibility(View.VISIBLE);
            name_error.requestFocus();
        }else if (Objects.requireNonNull(phone_input.getText()).toString().trim().equals("")) {
            phone_error.setVisibility(View.VISIBLE);
            phone_error.requestFocus();
        }else if (Objects.requireNonNull(phone_input.getText()).toString().trim().length() <= 8) {
            phone_error.setText(R.string.phone_error_txt);
            phone_error.setVisibility(View.VISIBLE);
            phone_error.requestFocus();
        }else if (customSpinner.getSelectedItemId() == 0) {
            gender_error.setVisibility(View.VISIBLE);
            gender_error.requestFocus();
        }else{
            gender_error.setVisibility(View.GONE);
            phone_error.setVisibility(View.GONE);
            name_error.setVisibility(View.GONE);

            lDialog1 = new SweetAlertDialog(UpdateProfile.this, SweetAlertDialog.WARNING_TYPE);
            lDialog1.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog1.setTitleText("Please make sure that the phone number you have entered is currently active on your device for verification!");
            lDialog1.setCancelable(true);
            lDialog1.show();
            lDialog1.setConfirmClickListener(sweetAlertDialog -> {
                lDialog1.dismiss();
                verifyPhone();
            });
            lDialog1.setCancelClickListener(sweetAlertDialog -> lDialog1.dismiss());

        }
    }

    private void verifyPhone() {

       userPhone = Objects.requireNonNull(phone_input.getText()).toString().trim();
        String userName = username.getEditableText().toString().trim();
        String userCountry = ccp.getSelectedCountryName();
        userPhone = Objects.requireNonNull(phone_input.getText()).toString().trim();

        Intent intent = new Intent(UpdateProfile.this, VerifyOTP.class);
        Bundle bundle = new Bundle();
        bundle.putString("phone_input", userPhone);
        bundle.putString("user_country", userCountry);
        bundle.putString("user_gender", genderItem.getmGenderName());
        bundle.putString("user_name", userName);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

}

    private void listeners() {
        ccp.setOnCountryChangeListener(() -> phone_input.setText(ccp.getSelectedCountryCodeWithPlus()));
    }

    private void initList() {
        mGenderList = new ArrayList<>();
        mGenderList.add(new GenderItem("--SELECT GENDER--", R.drawable.account));
        mGenderList.add(new GenderItem("MALE", R.drawable.account));
        mGenderList.add(new GenderItem("FEMALE", R.drawable.account));
    }


}