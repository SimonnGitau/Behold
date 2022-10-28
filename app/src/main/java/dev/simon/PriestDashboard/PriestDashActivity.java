package dev.simon.PriestDashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.PriestDashboard.Activities.AddEventActivity;
import dev.simon.PriestDashboard.Activities.AddTeachingActivity;
import dev.simon.PriestDashboard.Activities.AddVideoActivity;
import dev.simon.PriestDashboard.Activities.AddYTVideoActivity;
import dev.simon.PriestDashboard.Fragments.MyAccountFragment;
import dev.simon.PriestDashboard.Fragments.MyEventsFragment;
import dev.simon.PriestDashboard.Fragments.MyHomeFragment;
import dev.simon.PriestDashboard.Fragments.MyTeachingsFragment;
import dev.simon.PriestDashboard.Fragments.MyVideosFragment;
import dev.simon.behold.AuthActivity;
import dev.simon.behold.R;
import nl.joery.animatedbottombar.AnimatedBottomBar;

public class PriestDashActivity extends AppCompatActivity {

    private AddFloatingActionButton add_video, add_event, add_teaching, add_youtube;
    private FloatingActionsMenu fab_menu;
    private SweetAlertDialog lDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priest_dash);

        add_video = findViewById(R.id.add_video);
        fab_menu = findViewById(R.id.fab_menu);
        add_event = findViewById(R.id.add_event);
        add_teaching = findViewById(R.id.add_teaching);
        add_youtube = findViewById(R.id.add_youtube);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        add_youtube.setOnClickListener(View ->{
            fab_menu.collapse();
            Intent intent = new Intent(PriestDashActivity.this, AddYTVideoActivity.class);
            startActivity(intent);
        });

        add_video.setOnClickListener(View ->{
            fab_menu.collapse();
            Intent intent = new Intent(PriestDashActivity.this, AddVideoActivity.class);
            startActivity(intent);
        });

        add_event.setOnClickListener(View ->{
            fab_menu.collapse();
            Intent intent = new Intent(PriestDashActivity.this, AddEventActivity.class);
            startActivity(intent);
        });

        add_teaching.setOnClickListener(View ->{
            fab_menu.collapse();
            Intent intent = new Intent(PriestDashActivity.this, AddTeachingActivity.class);
            startActivity(intent);
        });

        //Navigation Drawer
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        replaceFragment(new MyHomeFragment());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);

                switch (id) {
                    case R.id.my_home:
                        replaceFragment(new MyHomeFragment());
                        break;
                    case R.id.my_events:
                        replaceFragment(new MyEventsFragment());
                        break;
                    case R.id.my_teachings:
                        replaceFragment(new MyTeachingsFragment());
                        break;
                    case R.id.my_videos:
                        replaceFragment(new MyVideosFragment());
                        break;
                    case R.id.my_account:
                        replaceFragment(new MyAccountFragment());
                        break;
                    case R.id.log_out:
                        logOut();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    private void logOut() {
        lDialog = new SweetAlertDialog(PriestDashActivity.this, SweetAlertDialog.WARNING_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("You will be logged out!");
        lDialog.setCancelable(true);
        lDialog.show();
        lDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                lDialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PriestDashActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });
        lDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                lDialog.dismiss();
            }
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }
}