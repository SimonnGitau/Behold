package dev.simon.UserDashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import dev.simon.behold.R;
import dev.simon.UserDashboard.Adapter.DashAdapter;
import nl.joery.animatedbottombar.AnimatedBottomBar;

public class UserDashActivity extends AppCompatActivity {

    private ViewPager2 dash_view_pager;
    private AnimatedBottomBar bottom_nav_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash);

        dash_view_pager = findViewById(R.id.dash_view_pager);
        bottom_nav_bar = findViewById(R.id.bottom_nav_bar);

        DashAdapter viewPagerAdapter = new DashAdapter(UserDashActivity.this);
        dash_view_pager.setAdapter(viewPagerAdapter);
        dash_view_pager.setCurrentItem(2);
        dash_view_pager.setUserInputEnabled(false);

        bottom_nav_bar.setupWithViewPager2(dash_view_pager);
        bottom_nav_bar.setSelected(true);

    }
}