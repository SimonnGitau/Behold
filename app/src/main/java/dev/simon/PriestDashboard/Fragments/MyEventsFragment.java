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
import dev.simon.PriestDashboard.Adapters.MyEventAdapter;
import dev.simon.PriestDashboard.Adapters.MyTeachingAdapter;
import dev.simon.behold.R;

public class MyEventsFragment extends Fragment {

    private RecyclerView my_events_rc;
    private SweetAlertDialog lDialog;
    private LottieAnimationView empty_eve_img;
    private TextView empty_eve_txt;
    private SwipeRefreshLayout eve_ref_ly;

    List<Event> eventList;

    MyEventAdapter myEventAdapter;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        my_events_rc = view.findViewById(R.id.my_events_rc);
        empty_eve_img = view.findViewById(R.id.empty_eve_img);
        empty_eve_txt = view.findViewById(R.id.empty_eve_txt);
        eve_ref_ly = view.findViewById(R.id.eve_ref_ly);

        eventList = new ArrayList<>();
        myEventAdapter = new MyEventAdapter(getContext(), eventList);

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);
        lDialog.show();

        updateItems();

        eve_ref_ly.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                eve_ref_ly.setRefreshing(false);
                updateItems();
                myEventAdapter.notifyDataSetChanged();
                eventList.clear();
                lDialog.show();
            }
        });

        return view;
    }

    private void updateItems() {
        FirebaseFirestore.getInstance().collection("Events")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null){
                            eventList.clear();
                            lDialog.dismiss();
                            for (DocumentChange documentChange : value.getDocumentChanges()){
                                if (documentChange.getType() == DocumentChange.Type.ADDED){
                                    Event new_event = documentChange.getDocument().toObject(Event.class);
                                    eventList.add(new_event);
                                    my_events_rc.setAdapter(myEventAdapter);
                                    my_events_rc.setHasFixedSize(true);

                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                    my_events_rc.setLayoutManager(linearLayoutManager);
                                    //user_Dash_Items_Rc.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                    my_events_rc.setNestedScrollingEnabled(false);

                                    if (myEventAdapter.getItemCount() == 0){
                                        lDialog.dismiss();
                                        my_events_rc.setVisibility(View.GONE);
                                        empty_eve_txt.setVisibility(View.VISIBLE);
                                        empty_eve_img.setVisibility(View.VISIBLE);
                                    }else{
                                        lDialog.dismiss();
                                        my_events_rc.setVisibility(View.VISIBLE);
                                        empty_eve_txt.setVisibility(View.GONE);
                                        empty_eve_img.setVisibility(View.GONE);
                                    }
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
        myEventAdapter.notifyDataSetChanged();
        eventList.clear();
        lDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateItems();
        myEventAdapter.notifyDataSetChanged();
        eventList.clear();
        lDialog.show();
    }

}