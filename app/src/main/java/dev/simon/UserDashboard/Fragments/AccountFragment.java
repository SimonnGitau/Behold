package dev.simon.UserDashboard.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.UserDashboard.Activities.EditProfile;
import dev.simon.behold.AuthActivity;
import dev.simon.behold.MainActivity;
import dev.simon.behold.R;

public class AccountFragment extends Fragment {

    private MaterialButton edit_profile, log_out, del_acc, to_settings;

    private SweetAlertDialog lDialog;

    private FirebaseFirestore db;

    String user_id;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        db = FirebaseFirestore.getInstance();

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        to_settings = view.findViewById(R.id.to_settings);
        edit_profile = view.findViewById(R.id.edit_profile);
        log_out = view.findViewById(R.id.log_out);
        del_acc = view.findViewById(R.id.del_acc);

        edit_profile.setOnClickListener(View ->{
            editProfile();
        });

        log_out.setOnClickListener(View ->{

            lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("Confirm log out.");
            lDialog.setCancelable(true);
            lDialog.show();
            lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    lDialog.dismiss();
                    logOutUser();
                    startActivity(new Intent(requireContext(), AuthActivity.class));
                }
            });
        });

        del_acc.setOnClickListener(View ->{

            lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE);
            lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
            lDialog.setTitleText("You sure you want to delete account? This action is irreversible.");
            lDialog.setCancelable(true);
            lDialog.show();
            lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    lDialog.dismiss();
                    //removeUserStorage();
                    deleteUser();
                }
            });
        });

        return view;
    }

    private void editProfile() {
        startActivity(new Intent(getContext(), EditProfile.class));
    }

    private void deleteUser() {

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Deleting account...");
        lDialog.setCancelable(false);
        lDialog.show();

        db.collection("Users").document(user_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        removeUserDetails();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE);
                        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        lDialog.setTitleText(e.getMessage());
                        lDialog.setCancelable(true);
                        lDialog.show();
                    }
                });



    }

    private void removeUserStorage() {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .child("dp_img");

        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE);
                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                lDialog.setTitleText(e.getMessage());
                lDialog.setCancelable(true);
                lDialog.show();
            }
        });

    }

    private void removeUserDetails() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            destroy();
                        }
                    }
                });
    }

    private void destroy() {
        startActivity(new Intent(requireContext(), MainActivity.class));
    }

    private void logOutUser() {
        startActivity(new Intent(requireContext(), AuthActivity.class));

    }
}