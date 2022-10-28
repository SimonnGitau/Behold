package dev.simon.UserDashboard.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Channel;
import dev.simon.Classes.Event;
import dev.simon.Classes.Teach;
import dev.simon.Classes.User;
import dev.simon.Classes.VidYou;
import dev.simon.Classes.Video;
import dev.simon.UserDashboard.Activities.VideoPlayActivity;
import dev.simon.UserDashboard.Adapter.EventsAdapter;
import dev.simon.UserDashboard.Adapter.PriestAdapter;
import dev.simon.UserDashboard.Adapter.TeachingsAdapter;
import dev.simon.UserDashboard.Adapter.VideosAdapter;
import dev.simon.UserDashboard.Adapter.YouAdapter;
import dev.simon.behold.R;

public class HomeFragment extends Fragment {

    private RecyclerView pri_rc, home_vid_rc, home_tea_rc, home_you_rc, home_eve_rc;
    private SweetAlertDialog lDialog;
    private TextView home_vids, home_teas, home_you, home_eve, empty_teach_txt;
    private LottieAnimationView empty_teach_img;
    private SwipeRefreshLayout home_ref;

    //Pastor
    PriestAdapter priestAdapter;
    List<Channel> listChan;

    //Videos
    DatabaseReference myRef;
    FirebaseDatabase database;

    List<Video> listVideos;
    VideosAdapter videosAdapter;

    //Tea
    List<Teach> listTeach;
    TeachingsAdapter teachingsAdapter;

    //Youtube
    DatabaseReference my_you_ref;
    FirebaseDatabase you_database;

    //Events
    EventsAdapter eventsAdapter;
    List<Event> listEvents;

    List<VidYou> vidYous;

    YouAdapter myVideoAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);
        lDialog.show();

        pri_rc = view.findViewById(R.id.pri_rc);
        home_vid_rc = view. findViewById(R.id.home_vid_rc);
        home_tea_rc = view. findViewById(R.id.home_tea_rc);
        home_you_rc = view. findViewById(R.id.home_you_rc);
        home_eve_rc = view. findViewById(R.id.home_eve_rc);
        home_ref = view.findViewById(R.id.home_ref);

        empty_teach_img = view.findViewById(R.id.empty_teach_img);

        //All strings
        home_vids = view.findViewById(R.id.home_vids);
        home_teas = view.findViewById(R.id.home_teas);
        home_you = view.findViewById(R.id.home_you);
        home_eve = view.findViewById(R.id.home_eve);

        empty_teach_txt = view.findViewById(R.id.empty_teach_txt);

        //Pastor
        listChan = new ArrayList<>();
        priestAdapter = new PriestAdapter(getContext(), listChan);

        //Videos
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("videos");

        home_vid_rc.setHasFixedSize(true);
        home_vid_rc.setItemViewCacheSize(5);
        home_vid_rc.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        listVideos = new ArrayList<>();
        videosAdapter = new VideosAdapter(getContext(), listVideos);
        home_vid_rc.setAdapter(videosAdapter);

        //Teachings
        listTeach = new ArrayList<>();
        teachingsAdapter = new TeachingsAdapter(getContext(), listTeach);

        //Youtube
        you_database = FirebaseDatabase.getInstance();
        my_you_ref = you_database.getReference("SharedVideos");

        home_you_rc.setHasFixedSize(true);
        home_you_rc.setItemViewCacheSize(5);
        home_you_rc.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        vidYous = new ArrayList<>();
        myVideoAdapter = new YouAdapter(getContext(), vidYous);
        home_you_rc.setAdapter(myVideoAdapter);

        //Events
        listEvents = new ArrayList<>();
        eventsAdapter = new EventsAdapter(getContext(), listEvents);

        listEvents.clear();
        vidYous.clear();
        listTeach.clear();
        listVideos.clear();
        listChan.clear();

        updateItems();

        home_ref.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listEvents.clear();
                vidYous.clear();
                listTeach.clear();
                listVideos.clear();
                listChan.clear();
                home_ref.setRefreshing(false);
                lDialog.show();
                updateItems();
            }
        });

        return view;
    }

    private void updateItems() {

        channell();
        updateVideos();
        updateTeach();
        updateYou();
        updateEvents();

        empty_teach_img.setVisibility(View.GONE);
        empty_teach_txt.setVisibility(View.GONE);

    }

    private void updateEvents() {
        FirebaseFirestore.getInstance().collection("Events")
                .addSnapshotListener((value, error) -> {
                    if (value != null){
                        lDialog.dismiss();
                        listEvents.clear();
                        for (DocumentChange documentChange : value.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                Event new_event = documentChange.getDocument().toObject(Event.class);
                                listEvents.add(new_event);
                                home_eve_rc.setAdapter(eventsAdapter);
                                home_eve_rc.setHasFixedSize(true);

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                home_eve_rc.setLayoutManager(linearLayoutManager);
                                //user_Dash_Items_Rc.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                home_eve_rc.setNestedScrollingEnabled(false);
                            }
                        }
                    }
                });
    }

    private void updateYou() {
        my_you_ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vidYous.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    VidYou video = dataSnapshot.getValue(VidYou.class);

                    if (dataSnapshot.getValue() != null){
                        lDialog.dismiss();
                        vidYous.add(video);
                    }else {
                        lDialog.dismiss();
                    }
                    myVideoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lDialog.dismiss();
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTeach() {
        FirebaseFirestore.getInstance().collection("Teachings")
                .addSnapshotListener((value, error) -> {
                    if (value != null){
                        lDialog.dismiss();
                        listTeach.clear();
                        for (DocumentChange documentChange : value.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                Teach new_teach = documentChange.getDocument().toObject(Teach.class);
                                listTeach.add(new_teach);
                                home_tea_rc.setAdapter(teachingsAdapter);
                                home_tea_rc.setHasFixedSize(true);

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                                home_tea_rc.setLayoutManager(linearLayoutManager);
                                //user_Dash_Items_Rc.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                home_tea_rc.setNestedScrollingEnabled(false);
                            }
                        }
                    }
                });
    }

    private void updateVideos() {

        listVideos.clear();

        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Video videos = dataSnapshot.getValue(Video.class);
                    if (dataSnapshot.getValue() != null){
                        lDialog.dismiss();
                        listVideos.add(videos);
                        home_vid_rc.setVisibility(View.VISIBLE);
                    }else {
                        lDialog.dismiss();
                        home_vid_rc.setVisibility(View.GONE);
                    }
                    videosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lDialog.dismiss();
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void channell() {

        FirebaseFirestore.getInstance().collection("Channels")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        lDialog.dismiss();
                        listChan.clear();
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                Channel new_chan = documentChange.getDocument().toObject(Channel.class);
                                listChan.add(new_chan);
                                pri_rc.setAdapter(priestAdapter);
                                pri_rc.setHasFixedSize(true);

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                pri_rc.setLayoutManager(linearLayoutManager);
                                //user_Dash_Items_Rc.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                pri_rc.setNestedScrollingEnabled(false);

                            }
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        updateItems();
    }
}