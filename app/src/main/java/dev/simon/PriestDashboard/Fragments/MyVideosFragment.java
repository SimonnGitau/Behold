package dev.simon.PriestDashboard.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

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
import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Video;
import dev.simon.PriestDashboard.Adapters.MyVideoAdapter;
import dev.simon.PriestDashboard.Adapters.PriestVideosAdapters;
import dev.simon.behold.R;

public class MyVideosFragment extends Fragment {

    private ViewPager2 chats_vp;

    public MyVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_videos, container, false);


        chats_vp = view.findViewById(R.id.chats_vp);

        PriestVideosAdapters viewPagerAdapter = new PriestVideosAdapters(MyVideosFragment.this);
        chats_vp.setAdapter(viewPagerAdapter);

        return view;

    }

}