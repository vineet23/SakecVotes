package com.sakec.chembur.sakecvotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    TextView title;
    ImageView imageView;
    public static String name,email,token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title_bar);
        imageView = findViewById(R.id.webImage);

        title.setText(getString(R.string.app_name));
        loadFragment(MainFragment.getInstance());
        bottomNavigationView.setOnNavigationItemSelectedListener(itemSelectedListener);
        bottomNavigationView.setOnNavigationItemReselectedListener(reselectedListener);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        token = getIntent().getStringExtra("token");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,WebActivity.class));
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_one:
                    loadFragment(MainFragment.getInstance());
                    title.setText(getString(R.string.app_name));
                    return true;
                case R.id.navigation_two:
                    loadFragment(CreatePollFragment.getInstance());
                    title.setText("Create Poll");
                    return true;
                case R.id.navigation_three:
                    loadFragment(MyPollsFragment.getInstance());
                    title.setText("My Polls");
                    return true;
                case R.id.navigation_four:
                    loadFragment(SettingsFragment.getInstance());
                    title.setText("Settings");
                    return true;
            }
            return false;
        }
    };

    private BottomNavigationView.OnNavigationItemReselectedListener reselectedListener
            = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_one:
                    loadFragment(MainFragment.refresh());
                    break;
                case R.id.navigation_three:
                    loadFragment(MyPollsFragment.refresh());
                    break;
            }
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}
