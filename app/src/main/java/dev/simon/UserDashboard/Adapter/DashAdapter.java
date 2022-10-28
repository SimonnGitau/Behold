package dev.simon.UserDashboard.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import dev.simon.UserDashboard.Fragments.AccountFragment;
import dev.simon.UserDashboard.Fragments.EventsFragment;
import dev.simon.UserDashboard.Fragments.HomeFragment;
import dev.simon.UserDashboard.Fragments.TeachingsFragment;
import dev.simon.UserDashboard.Fragments.WatchFragment;
import dev.simon.UserDashboard.UserDashActivity;

public class DashAdapter extends FragmentStateAdapter {

    public DashAdapter(@NonNull UserDashActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new EventsFragment();
            case 1:
                return new TeachingsFragment();
            case 2:
                return new HomeFragment();
            case 3:
                return new WatchFragment();
            case 4:
                return new AccountFragment();

        }
        return new HomeFragment();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
