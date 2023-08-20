package com.example.aimers.Views.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TeacherMainActivity extends AppCompatActivity {

    private AppBar appBar;
    private Toolbar toolbar;


    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        init();
        appBar.init(toolbar, "Home");
        appBar.hideBackButton();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new TeacherHomeFragment()).commit();
        navigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.menu_homeId: {
                        selectedFragment = new TeacherHomeFragment();
                        appBar.setAppBarText("Home");
                        break;
                    }
                    case R.id.menu_NoticeId: {
                        selectedFragment = new TeacherNoticeFragment();
                        appBar.setAppBarText("Notice");
                        break;
                    }
                    case R.id.menu_teacherProfileId: {
                        selectedFragment = new TeacherProfileFragment();
                        appBar.setAppBarText("Profile");
                        break;
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,
                        selectedFragment).commit();
                return true;
            }
        });
    }

    private void init() {
        appBar = new AppBar(this);
        toolbar = findViewById(R.id.appbarId);

        //navigation
        navigationView = findViewById(R.id.main_nav);
    }

}