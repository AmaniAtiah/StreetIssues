package com.barmej.streetissues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton mAddIssuesBtn;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        mAddIssuesBtn = findViewById(R.id.button_add_new_issues);
        bottomNavigationView = findViewById(R.id.navigation_bottom);

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new IssuesListFragment()).commit();


        mAddIssuesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddNewIssueActivity.class));
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNav =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_issues:
                    selectedFragment = new IssuesListFragment();
                    break;

                case R.id.nav_map:
                    selectedFragment = new IssuesMapFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.content, selectedFragment).commit();
            return true;
        }
    };
}