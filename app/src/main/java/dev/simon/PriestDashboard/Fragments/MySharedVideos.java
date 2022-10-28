package dev.simon.PriestDashboard.Fragments;

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
import dev.simon.Classes.Video;
import dev.simon.PriestDashboard.Adapters.MyVideoAdapter;
import dev.simon.behold.R;


public class MySharedVideos extends Fragment {

    private RecyclerView new_vids_upload;
    private SweetAlertDialog lDialog;
    private SwipeRefreshLayout myVidRefreshLayout;

    private LottieAnimationView empty_my_vids_img;
    private TextView empty_my_vids_txt;

    DatabaseReference myRef;
    FirebaseDatabase database;

    List<Video> videoList;

    MyVideoAdapter myVideoAdapter;

    public MySharedVideos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_shared_videos, container, false);

        new_vids_upload = view.findViewById(R.id.new_vids_upload);
        empty_my_vids_img = view.findViewById(R.id.empty_my_vids_img);
        empty_my_vids_txt = view.findViewById(R.id.empty_my_vids_txt);
        myVidRefreshLayout = view.findViewById(R.id.myVidsRef);

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(false);
        lDialog.show();


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("videos");

        new_vids_upload.setHasFixedSize(true);
        new_vids_upload.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        videoList = new ArrayList<>();
        myVideoAdapter = new MyVideoAdapter(getContext(), videoList);
        new_vids_upload.setAdapter(myVideoAdapter);

        updateMyVideos();

        myVidRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                myVidRefreshLayout.setRefreshing(false);
                updateMyVideos();
                myVideoAdapter.notifyDataSetChanged();
                videoList.clear();
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

                lDialog.dismiss();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Video video = dataSnapshot.getValue(Video.class);

                    if (dataSnapshot.getValue() != null){
                        lDialog.dismiss();
                        videoList.add(video);
                        new_vids_upload.setVisibility(View.VISIBLE);
                        empty_my_vids_txt.setVisibility(View.GONE);
                        empty_my_vids_img.setVisibility(View.GONE);
                    }else {
                        lDialog.dismiss();
                        new_vids_upload.setVisibility(View.GONE);
                        empty_my_vids_txt.setVisibility(View.VISIBLE);
                        empty_my_vids_img.setVisibility(View.VISIBLE);
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