package dev.simon.UserDashboard.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import dev.simon.PriestDashboard.Fragments.MySharedVideos;
import dev.simon.PriestDashboard.Fragments.MyVideosFragment;
import dev.simon.PriestDashboard.Fragments.MyYoutubeFragment;
import dev.simon.UserDashboard.Fragments.FromYoutubeFragment;
import dev.simon.UserDashboard.Fragments.VideosFragment;
import dev.simon.UserDashboard.Fragments.WatchFragment;

public class UserVideoAdapter extends FragmentStateAdapter {

    public UserVideoAdapter(@NonNull WatchFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new VideosFragment();
            case 1:
                return new FromYoutubeFragment();

        }
        return new VideosFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
