package dev.simon.UserDashboard.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import dev.simon.UserDashboard.Adapter.UserVideoAdapter;
import dev.simon.behold.R;

public class WatchFragment extends Fragment {

    private ViewPager2 vids_vp;
    private MaterialCardView shared_you;

    public WatchFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch, container, false);

        vids_vp = view.findViewById(R.id.vids_vp);
        shared_you = view.findViewById(R.id.shared_you);

        onRef();

        UserVideoAdapter viewPagerAdapter = new UserVideoAdapter(WatchFragment.this);
        vids_vp.setAdapter(viewPagerAdapter);

        return view;
    }

    private void onRef() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shared_you.setVisibility(View.GONE);
            }
        }, 10000);
    }

    @Override
    public void onStart() {
        super.onStart();
        onRef();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRef();
    }
}