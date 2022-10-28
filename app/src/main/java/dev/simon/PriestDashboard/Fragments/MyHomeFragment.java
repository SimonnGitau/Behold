package dev.simon.PriestDashboard.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Channel;
import dev.simon.UserDashboard.Adapter.PriestAdapter;
import dev.simon.behold.R;

public class MyHomeFragment extends Fragment {

    private RecyclerView priests_rc;
    private SweetAlertDialog lDialog;

    PriestAdapter priestAdapter;
    List<Channel> listChan;


    public MyHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_home, container, false);

        priests_rc = view.findViewById(R.id.priests_rc);

        listChan = new ArrayList<>();
        priestAdapter = new PriestAdapter(getContext(), listChan);

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);
        lDialog.show();

        updateItems();

        return view;

    }

    private void updateItems() {

        FirebaseFirestore.getInstance().collection("Channels")
                .addSnapshotListener((value, error) -> {
                    if (value != null){
                        lDialog.dismiss();
                        for (DocumentChange documentChange : value.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                Channel new_chan = documentChange.getDocument().toObject(Channel.class);
                                listChan.add(new_chan);
                                priests_rc.setAdapter(priestAdapter);
                                priests_rc.setHasFixedSize(true);

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                priests_rc.setLayoutManager(linearLayoutManager);
                                //user_Dash_Items_Rc.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                priests_rc.setNestedScrollingEnabled(false);

                            }
                        }
                    }
                });

    }
}