package com.yellion.yellapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.navigation.NavigationBarView;
import com.yellion.yellapp.models.BudgetCard;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, View.OnClickListener {

    static int currentFragmentPosition = -1;
    static SharedPreferences sharedPreferences = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavListener();
    }
    private void setNavListener()
    {
        ImageButton btn = (ImageButton) findViewById(R.id.navHome);
        btn.setOnClickListener(this);
        btn.performClick();
        btn = (ImageButton) findViewById(R.id.navDashboards);
        btn.setOnClickListener(this);
        btn = (ImageButton) findViewById(R.id.navBudgets);
        btn.setOnClickListener(this);
        btn = (ImageButton) findViewById(R.id.navAccount);
        btn.setOnClickListener(this);
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
                selected = new ListBudgetsFragment();
                nextPosition = 1;
                break;
            case R.id.navBudgets:
                selected = new ListBudgetsFragment();
//                        new BudgetsFragment(new BudgetCard("Sổ tay chi tiêu",20000,20000,"yyyy/mm/dd","yyyy/mm/dd","yyyy/mm/dd"));
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
                        .replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
            }
            if(currentFragmentPosition > newPosition) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                ((FragmentTransaction) transaction).setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right );
                transaction.replace(R.id.fragmentContainer, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            if(currentFragmentPosition < newPosition) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.fragmentContainer, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
            currentFragmentPosition = newPosition;
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        Fragment selected = null;
        int nextPosition = currentFragmentPosition;
        int viewId = view.getId();
        if (viewId == R.id.navHome) {
            selected = new HomeFragment();
            nextPosition = 0;
        }
        else if (viewId == R.id.navDashboards) {
            selected = new ListBudgetsFragment();
            nextPosition = 1;
        }
        else if (viewId == R.id.navBudgets) {
            selected = new BudgetsFragment(new BudgetCard("Sổ tay chi tiêu",20000,20000,"yyyy/mm/dd","yyyy/mm/dd","yyyy/mm/dd" ));
            nextPosition = 2;
        }
        else if (viewId == R.id.navAccount) {
            selected = new AccountFragment();
            nextPosition = 3;
        }

        if (currentFragmentPosition != nextPosition) {
            ImageButton btn = null;
            switch (currentFragmentPosition)
            {
                case 0:
                    btn = (ImageButton) findViewById(R.id.navHome);
                    btn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_yellow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    break;
                case 1:
                    btn = (ImageButton) findViewById(R.id.navDashboards);
                    btn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_yellow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    break;
                case 2:
                    btn = (ImageButton) findViewById(R.id.navBudgets);
                    btn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_yellow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    break;
                case 3:
                    btn = (ImageButton) findViewById(R.id.navAccount);
                    btn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_yellow),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    break;
            }

            loadFragment(selected, nextPosition);

            btn = (ImageButton) findViewById(viewId);
            btn.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}