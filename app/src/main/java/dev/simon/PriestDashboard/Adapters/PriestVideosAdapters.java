package dev.simon.PriestDashboard.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import dev.simon.PriestDashboard.Fragments.MySharedVideos;
import dev.simon.PriestDashboard.Fragments.MyVideosFragment;
import dev.simon.PriestDashboard.Fragments.MyYoutubeFragment;

public class PriestVideosAdapters extends FragmentStateAdapter {

    public PriestVideosAdapters(@NonNull MyVideosFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MySharedVideos();
            case 1:
                return new MyYoutubeFragment();

        }
        return new MySharedVideos();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
