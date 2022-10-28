package dev.simon.PriestDashboard.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Event;
import dev.simon.Classes.Teach;
import dev.simon.Classes.Video;
import dev.simon.PriestDashboard.Adapters.MyEventAdapter;
import dev.simon.PriestDashboard.Adapters.MyTeachingAdapter;
import dev.simon.PriestDashboard.Adapters.MyVideoAdapter;
import dev.simon.behold.R;

public class MyTeachingsFragment extends Fragment {

    private RecyclerView my_teach_rc;
    private SweetAlertDialog lDialog;
    private LottieAnimationView empty_tea_img;
    private TextView empty_tea_txt;
    private SwipeRefreshLayout tes_ref_ly;

    List<Teach> teachList;

    MyTeachingAdapter myTeachingAdapter;

    public MyTeachingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_teachings, container, false);

        my_teach_rc = view.findViewById(R.id.my_teach_rc);
        empty_tea_img = view.findViewById(R.id.empty_tea_img);
        empty_tea_txt = view.findViewById(R.id.empty_tea_txt);
        tes_ref_ly = view.findViewById(R.id.tes_ref_ly);

        teachList = new ArrayList<>();

        myTeachingAdapter = new MyTeachingAdapter(getContext(), teachList);

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);
        lDialog.show();

        teachList.clear();

        updateItems();

        tes_ref_ly.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                tes_ref_ly.setRefreshing(false);
                updateItems();
                myTeachingAdapter.notifyDataSetChanged();
                teachList.clear();
                lDialog.show();
            }
        });

        return view;
    }

    private void updateItems() {
        FirebaseFirestore.getInstance().collection("Teachings")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null){
                            lDialog.dismiss();
                            for (DocumentChange documentChange : value.getDocumentChanges()){
                                if (documentChange.getType() == DocumentChange.Type.ADDED){
                                    Teach new_teaching = documentChange.getDocument().toObject(Teach.class);
                                    teachList.clear();
                                    teachList.add(new_teaching);
                                    my_teach_rc.setAdapter(myTeachingAdapter);
                                    my_teach_rc.setHasFixedSize(true);

                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                    my_teach_rc.setLayoutManager(linearLayoutManager);
                                    //user_Dash_Items_Rc.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                    my_teach_rc.setNestedScrollingEnabled(false);

                                    if (myTeachingAdapter.getItemCount() == 0){
                                        lDialog.dismiss();
                                        my_teach_rc.setVisibility(View.GONE);
                                        empty_tea_img.setVisibility(View.VISIBLE);
                                        empty_tea_txt.setVisibility(View.VISIBLE);
                                    }else{
                                        lDialog.dismiss();
                                        my_teach_rc.setVisibility(View.VISIBLE);
                                        empty_tea_img.setVisibility(View.GONE);
                                        empty_tea_txt.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        lDialog.show();
        updateItems();
        myTeachingAdapter.notifyDataSetChanged();
        teachList.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        lDialog.show();
        updateItems();
        myTeachingAdapter.notifyDataSetChanged();
        teachList.clear();

    }
}