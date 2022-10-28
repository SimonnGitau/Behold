package dev.simon.UserDashboard.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.VidYou;
import dev.simon.PriestDashboard.Adapters.MyYouVidsAdapter;
import dev.simon.UserDashboard.Adapter.YouAdapter;
import dev.simon.behold.R;


public class FromYoutubeFragment extends Fragment {

    private RecyclerView new_youVid_upload;
    private SweetAlertDialog lDialog;
    private SwipeRefreshLayout myVidRefreshLayout;

    private LottieAnimationView empty_youVid_img;
    private TextView empty_my_youVid_txt;

    DatabaseReference myRef;
    FirebaseDatabase database;

    List<VidYou> vidYous;

    YouAdapter myVideoAdapter;


    public FromYoutubeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_from_youtube, container, false);

        new_youVid_upload = view.findViewById(R.id.new_youVid_upload);
        myVidRefreshLayout = view.findViewById(R.id.myYouRef);
        empty_youVid_img = view.findViewById(R.id.empty_youVid_img);
        empty_my_youVid_txt = view.findViewById(R.id.empty_my_youVid_txt);

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SharedVideos");

        new_youVid_upload.setHasFixedSize(true);
        new_youVid_upload.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        vidYous = new ArrayList<>();
        myVideoAdapter = new YouAdapter(getContext(), vidYous);
        new_youVid_upload.setAdapter(myVideoAdapter);

        updateMyVideos();

        myVidRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                myVidRefreshLayout.setRefreshing(false);
                updateMyVideos();
                myVideoAdapter.notifyDataSetChanged();
                vidYous.clear();
                lDialog.show();
            }
        });


        return view;
    }

    private void updateMyVideos() {

        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    VidYou video = dataSnapshot.getValue(VidYou.class);

                    if (dataSnapshot.getValue() != null){
                        lDialog.dismiss();
                        vidYous.add(video);
                        new_youVid_upload.setVisibility(View.VISIBLE);
                        empty_my_youVid_txt.setVisibility(View.GONE);
                        empty_youVid_img.setVisibility(View.GONE);
                    }else {
                        lDialog.dismiss();
                        new_youVid_upload.setVisibility(View.GONE);
                        empty_my_youVid_txt.setVisibility(View.VISIBLE);
                        empty_youVid_img.setVisibility(View.VISIBLE);
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
}