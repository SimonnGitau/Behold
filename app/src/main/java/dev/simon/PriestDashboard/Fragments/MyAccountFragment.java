package dev.simon.PriestDashboard.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.UserDashboard.UserDashActivity;
import dev.simon.behold.R;

public class MyAccountFragment extends Fragment {

    private RelativeLayout switch_acc;
    private SweetAlertDialog lDialog;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        switch_acc = view.findViewById(R.id.switch_acc);

        switch_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE);
                lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                lDialog.setTitleText("Continue as a user?");
                lDialog.setCancelable(true);
                lDialog.show();
                lDialog.setConfirmClickListener(sweetAlertDialog -> {
                    try {
                        lDialog.dismiss();
                        startActivity(new Intent(requireContext(), UserDashActivity.class));
                        MyAccountFragment.this.finalize();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE);
                        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        lDialog.setTitleText(e.getLocalizedMessage());
                        lDialog.setCancelable(true);
                        lDialog.show();
                    }
                });
            }
        });

        return view;
    }
}