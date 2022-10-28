package dev.simon.behold;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Arrays;

public class PrivacyTerms extends AppCompatActivity {

    private MaterialCheckBox check_age, check_updates;
    private MaterialButton acc_terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_terms);

        acc_terms = findViewById(R.id.acc_terms);
        check_updates = findViewById(R.id.check_updates);
        check_age = findViewById(R.id.check_age);

        acc_terms.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        acc_terms.setTextColor(ContextCompat.getColor(getApplicationContext(), cn.pedant.SweetAlert.R.color.gray_btn_bg_color));

        for (MaterialCheckBox checkBox: Arrays.asList(check_age, check_updates)){
           checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
               if (check_updates.isChecked()  && check_age.isChecked()){
                   check_updates.setChecked(true);
                   check_age.setChecked(true);
                   acc_terms.setCheckable(true);
                   check_age.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                   check_updates.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                   acc_terms.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
                   acc_terms.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
               }else if (!check_updates.isChecked()  && !check_age.isChecked()){
                   check_updates.setChecked(false);
                   check_age.setChecked(false);
                   acc_terms.setCheckable(false);
                   checkBox.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                   acc_terms.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                   acc_terms.setTextColor(ContextCompat.getColor(getApplicationContext(), cn.pedant.SweetAlert.R.color.gray_btn_bg_color));
               }else {
                   acc_terms.setCheckable(false);
                   checkBox.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                   acc_terms.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                   acc_terms.setTextColor(ContextCompat.getColor(getApplicationContext(), cn.pedant.SweetAlert.R.color.gray_btn_bg_color));
               }
           });
           
           acc_terms.setOnClickListener(View -> {
               if (acc_terms.isCheckable()){
                   startActivity(new Intent(PrivacyTerms.this, AuthActivity.class));
                   finish();
               }else{
                   Toast.makeText(this, "Please check all the fields first.", Toast.LENGTH_SHORT).show();
               }
           });
       }

    }
}