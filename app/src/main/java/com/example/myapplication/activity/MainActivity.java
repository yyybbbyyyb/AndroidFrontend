package com.example.myapplication.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.fragment.AIFragment;
import com.example.myapplication.fragment.AccountFragment;
import com.example.myapplication.fragment.ChartFragment;
import com.example.myapplication.fragment.DetailFragment;
import com.example.myapplication.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Fragment currentFragment = new DetailFragment();  // 初始化为默认Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置默认 Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commitAllowingStateLoss();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {

                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.navigation_detail) {
                    if (currentFragment.getClass().equals(DetailFragment.class)) {
                        return true;
                    }
                    selectedFragment = new DetailFragment();
                } else if (item.getItemId() == R.id.navigation_chart) {
                    if (currentFragment.getClass().equals(ChartFragment.class)) {
                        return true;
                    }
                    selectedFragment = new ChartFragment();
                } else if (item.getItemId() == R.id.navigation_account) {
                    if (currentFragment.getClass().equals(AccountFragment.class)) {
                        return true;
                    }
                    selectedFragment = new AccountFragment();
                } else if (item.getItemId() == R.id.navigation_ai) {
                    if (currentFragment.getClass().equals(AIFragment.class)) {
                        return true;
                    }
                    selectedFragment = new AIFragment();
                } else if (item.getItemId() == R.id.navigation_profile) {
                    if (currentFragment.getClass().equals(ProfileFragment.class)) {
                        return true;
                    }
                    selectedFragment = new ProfileFragment();
                }

                // 只在选中不同Fragment时切换
                if (selectedFragment != null) {
                    currentFragment = selectedFragment;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, currentFragment)
                            .commitAllowingStateLoss();
                }
                return true;
            }
        });
    }
}
