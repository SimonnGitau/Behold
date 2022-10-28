package dev.simon.UserDashboard.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Event;
import dev.simon.UserDashboard.Adapter.EventsAdapter;
import dev.simon.UserDashboard.Adapter.EventsFragAdapter;
import dev.simon.behold.R;

public class EventsFragment extends Fragment {

    private SwipeRefreshLayout events_ref;
    private RecyclerView events_rc;
    private LottieAnimationView empty_event_img;
    private TextView empty_event_txt;
    private SweetAlertDialog lDialog;

    EventsFragAdapter eventsAdapter;
    List<Event> listEvents;

    public EventsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        events_ref = view.findViewById(R.id.events_ref);
        events_rc = view.findViewById(R.id.events_rc);
        empty_event_img = view.findViewById(R.id.empty_event_img);
        empty_event_txt = view.findViewById(R.id.empty_event_txt);

        listEvents = new ArrayList<>();
        eventsAdapter = new EventsFragAdapter(getContext(), listEvents);

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);
        lDialog.show();

        updateItems();

        events_ref.setOnRefreshListener(() -> {
            events_ref.setRefreshing(false);
            updateItems();
            eventsAdapter.notifyDataSetChanged();
            listEvents.clear();
            lDialog.show();
        });

        return view;
    }

    private void updateItems() {

        FirebaseFirestore.getInstance().collection("Events")
                .addSnapshotListener((value, error) -> {
                    if (value != null){
                        lDialog.dismiss();
                        listEvents.clear();
                        for (DocumentChange documentChange : value.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                Event new_event = documentChange.getDocument().toObject(Event.class);
                                listEvents.add(new_event);
                                events_rc.setAdapter(eventsAdapter);
                                events_rc.setHasFixedSize(true);

                                //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                //events_rc.setLayoutManager(linearLayoutManager);
                                events_rc.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                events_rc.setNestedScrollingEnabled(false);

                                if (eventsAdapter.getItemCount() == 0){
                                    lDialog.dismiss();
                                    events_rc.setVisibility(View.GONE);
                                    empty_event_txt.setVisibility(View.VISIBLE);
                                    empty_event_img.setVisibility(View.VISIBLE);
                                }else{
                                    lDialog.dismiss();
                                    events_rc.setVisibility(View.VISIBLE);
                                    empty_event_txt.setVisibility(View.GONE);
                                    empty_event_img.setVisibility(View.GONE);
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
        eventsAdapter.notifyDataSetChanged();
        listEvents.clear();
        lDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateItems();
        eventsAdapter.notifyDataSetChanged();
        listEvents.clear();
        lDialog.show();
    }

}