package dev.simon.UserDashboard.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Event;
import dev.simon.Classes.Teach;
import dev.simon.UserDashboard.Adapter.EventsAdapter;
import dev.simon.UserDashboard.Adapter.TeachFragmentAdapter;
import dev.simon.UserDashboard.Adapter.TeachingsAdapter;
import dev.simon.behold.R;

public class TeachingsFragment extends Fragment {

    private SwipeRefreshLayout teach_ref;
    private RecyclerView teach_rc;
    private LottieAnimationView empty_teach_img;
    private TextView empty_teach_txt;
    private SweetAlertDialog lDialog;

    List<Teach> listTeach;
    TeachFragmentAdapter teachingsAdapter;

    public TeachingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teachings, container, false);

        teach_ref = view.findViewById(R.id.teach_ref);
        teach_rc = view.findViewById(R.id.teach_rc);
        empty_teach_img = view.findViewById(R.id.empty_teach_img);
        empty_teach_txt = view.findViewById(R.id.empty_teach_txt);

        listTeach = new ArrayList<>();
        teachingsAdapter = new TeachFragmentAdapter(getContext(), listTeach);

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);
        lDialog.show();

        updateItems();

        teach_ref.setOnRefreshListener(() -> {
            teach_ref.setRefreshing(false);
            updateItems();
            teachingsAdapter.notifyDataSetChanged();
            listTeach.clear();
            lDialog.show();
        });

        return view;
    }

    private void updateItems() {

        FirebaseFirestore.getInstance().collection("Teachings")
                .addSnapshotListener((value, error) -> {
                    if (value != null){
                        lDialog.dismiss();
                        listTeach.clear();
                        for (DocumentChange documentChange : value.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                Teach new_teach = documentChange.getDocument().toObject(Teach.class);
                                listTeach.add(new_teach);
                                teach_rc.setAdapter(teachingsAdapter);
                                teach_rc.setHasFixedSize(true);

                                //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                //teach_rc.setLayoutManager(linearLayoutManager);
                                teach_rc.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                teach_rc.setNestedScrollingEnabled(false);

                                if (teachingsAdapter.getItemCount() == 0){
                                    lDialog.dismiss();
                                    teach_rc.setVisibility(View.GONE);
                                    empty_teach_txt.setVisibility(View.VISIBLE);
                                    empty_teach_img.setVisibility(View.VISIBLE);
                                }else{
                                    lDialog.dismiss();
                                    teach_rc.setVisibility(View.VISIBLE);
                                    empty_teach_txt.setVisibility(View.GONE);
                                    empty_teach_img.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateItems();
        teachingsAdapter.notifyDataSetChanged();
        listTeach.clear();
        lDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateItems();
        teachingsAdapter.notifyDataSetChanged();
        listTeach.clear();
        lDialog.show();
    }

}