package com.yellion.yellapp;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.yellion.yellapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container,false);
        View view = binding.getRoot();

        binding.viewAllDashboardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                ListDashboardsFragment dashboardsFragment = new ListDashboardsFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment,dashboardsFragment, "LIST_DASHBOARD").addToBackStack(null).commit();
            }
        });

        binding.notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                NotificationFragment notificationFragment = new NotificationFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment,notificationFragment, "NOTIFICATION").addToBackStack(null).commit();
            }
        });

        return view;
    }
}