package com.example.myfeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.navigation.NavigationView;

public class mainApp_activity extends AppCompatActivity {


    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app_activity);


        Fragment defaultFragment= new homeFeed();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, defaultFragment).commit();


        bottomNav=findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;

                switch (item.getItemId()) {
                    case R.id.add_post_bottom_nav:
                        selectedFragment = new addPost();
                        break;
                    case R.id.profile:
                        selectedFragment = new profile();
                        break;

                    default:
                        selectedFragment = new homeFeed();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                return true;
            }
        });



    }


}
