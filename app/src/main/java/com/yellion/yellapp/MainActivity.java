package com.yellion.yellapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    static int currentFragmentPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        BottomNavigationView bnv = findViewById(R.id.bottomNavView);
        bnv.setOnItemSelectedListener(this);

        bnv.setSelectedItemId(R.id.navHome);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selected = null;
        int nextPosition = currentFragmentPosition;
        switch (item.getItemId())
        {
            case R.id.navHome:
                selected = new HomeFragment();
                nextPosition = 0;
                break;
            case R.id.navDashboards:
                selected = new DashboardsFragment();
                nextPosition = 1;
                break;
            case R.id.navBudgets:
                selected = new BudgetsFragment();
                nextPosition = 2;
                break;
            case R.id.navAccount:
                selected = new AccountFragment();
                nextPosition = 3;
                break;
        }

        return loadFragment(selected, nextPosition);
    }
    private boolean loadFragment(Fragment fragment, int newPosition) {
        if(fragment != null) {
            if(newPosition == currentFragmentPosition) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment).commit();
            }
            if(currentFragmentPosition > newPosition) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                ((FragmentTransaction) transaction).setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right );
                transaction.replace(R.id.fragmentContainer, fragment);
                transaction.commit();
            }
            if(currentFragmentPosition < newPosition) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.fragmentContainer, fragment);
                transaction.commit();
            }
            currentFragmentPosition = newPosition;
            return true;
        }

        return false;
    }
}