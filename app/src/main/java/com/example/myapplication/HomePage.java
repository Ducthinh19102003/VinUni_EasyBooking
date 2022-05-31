package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.module.AppGlideModule;
import com.example.myapplication.BookingProcess.OfficeHourBooking;
import com.example.myapplication.ProfessorSetAvailableTimeSlots.WeekViewActivity;
import com.example.myapplication.RoomBookingActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityHomePageBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity {
    private static final int MAGICAL_NUMBER = 8;
    TextView name,email;
    DrawerLayout drawer;

    FloatingActionButton officeHourFAB, discussionRoomFAB;
    ExtendedFloatingActionButton fab;
    TextView officeHourText, discussionRoomText;

    // to check whether sub FABs are visible or not
    Boolean isAllFabsVisible;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomePageBinding binding;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHomePage.toolbar);

        //fabs4life
        officeHourFAB = binding.appBarHomePage.officeHourFAB;
        discussionRoomFAB = binding.appBarHomePage.discussionRoomFAB;
        fab = binding.appBarHomePage.fab;
        officeHourText = binding.appBarHomePage.officeHourText;
        discussionRoomText = binding.appBarHomePage.discussionRoomText;

        fstore = FirebaseFirestore.getInstance();

        if (Login.portal == 2) {
            officeHourText.setText("Set Available Timeslots");
        }

        //fabs are invisible at first
        isAllFabsVisible = false;
        officeHourFAB.setVisibility(View.GONE);
        discussionRoomFAB.setVisibility(View.GONE);
        officeHourText.setVisibility(View.GONE);
        discussionRoomText.setVisibility(View.GONE);
        fab.shrink();

        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_view_professors, R.id.nav_account, R.id.nav_calendar)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        View headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.txt_user_name);
        email = headerView.findViewById(R.id.txt_email);
        if (Login.portal == 1) {
            name.setText(Login.currentStudent.getName());
            email.setText(Login.currentStudent.getEmail());
        }
        else if (Login.portal == 2) {
            name.setText(Login.currentProfessor.getName());
            email.setText(Login.currentProfessor.getEmail());
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_page);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void changeFABsVisibility(View view) {
        if (!isAllFabsVisible){
            discussionRoomFAB.show();
            discussionRoomText.setVisibility(View.VISIBLE);
            officeHourFAB.show();
            officeHourText.setVisibility(View.VISIBLE);
            fab.extend();
            isAllFabsVisible = true;
        }
        else {
            officeHourText.setVisibility(View.GONE);
            discussionRoomFAB.hide();
            officeHourFAB.hide();
            discussionRoomText.setVisibility(View.GONE);
            fab.shrink();
            isAllFabsVisible = false;
        }
    }

    public void OfficeHour(View view) {
        if (Login.portal == 1) {
            Toast.makeText(this, "To Booking Office Hours", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OfficeHourBooking.class));
        }
        else if (Login.portal == 2) {
            Toast.makeText(this, "To Set Available Timeslots Page", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, WeekViewActivity.class));
        }
    }

    public void DiscussionRoom(View view) {
        Toast.makeText(this, "To Booking Discussion Room", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, RoomBookingActivity.class));
    }

}