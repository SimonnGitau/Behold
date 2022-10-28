package dev.simon.UserDashboard.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import dev.simon.UserDashboard.Adapter.VideosAdapter;
import dev.simon.UserDashboard.Adapter.VideosFragmentAdapter;
import dev.simon.behold.R;


public class VideosFragment extends Fragment {

    private RecyclerView videos_rc;
    private TextView empty_video_txt;
    private LottieAnimationView empty_video_img;
    private SweetAlertDialog lDialog;
    private SwipeRefreshLayout videos_ref;

    DatabaseReference myRef;
    FirebaseDatabase database;

    List<Video> listVideos;
    VideosFragmentAdapter videosAdapter;


    public VideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        videos_rc = view.findViewById(R.id.videos_rc);
        empty_video_txt = view.findViewById(R.id.empty_video_txt);
        empty_video_img = view.findViewById(R.id.empty_video_img);
        videos_ref = view.findViewById(R.id.videos_ref);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("videos");

        videos_rc.setHasFixedSize(true);
        videos_rc.setLayoutManager(new GridLayoutManager(getContext(), 2));

        listVideos = new ArrayList<>();
        videosAdapter = new VideosFragmentAdapter(getContext(), listVideos);
        videos_rc.setAdapter(videosAdapter);

        lDialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);
        lDialog.show();

        listVideos.clear();

        updateItems();

        videos_ref.setOnRefreshListener(() -> {
            videos_ref.setRefreshing(false);
            updateItems();
            videosAdapter.notifyDataSetChanged();
            listVideos.clear();
            lDialog.show();
        });


        return view;
    }

    private void updateItems() {

        listVideos.clear();

        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listVideos.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Video videos = dataSnapshot.getValue(Video.class);
                    if (dataSnapshot.getValue() != null){
                        lDialog.dismiss();
                        listVideos.add(videos);
                        videos_rc.setVisibility(View.VISIBLE);
                        empty_video_txt.setVisibility(View.GONE);
                        empty_video_img.setVisibility(View.GONE);
                    }else {
                        lDialog.dismiss();
                        videos_rc.setVisibility(View.GONE);
                        empty_video_txt.setVisibility(View.VISIBLE);
                        empty_video_img.setVisibility(View.VISIBLE);
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

    @Override
    public void onResume() {
        super.onResume();
        updateItems();
        videosAdapter.notifyDataSetChanged();
        listVideos.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateItems();
        videosAdapter.notifyDataSetChanged();
        listVideos.clear();
    }
}