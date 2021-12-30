package com.yellion.yellapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yellion.yellapp.adapters.DashboardsAdapter;
import com.yellion.yellapp.databinding.FragmentListDashboardsBinding;
import com.yellion.yellapp.models.DashboardCard;

import java.util.ArrayList;
import java.util.List;

public class ListDashboardsFragment extends Fragment {
    FragmentListDashboardsBinding binding;
    DashboardsAdapter dashboardsAdapter = null;
    List<DashboardCard> list;


    public ListDashboardsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListDashboardsBinding.inflate(inflater, container, false );
        View view = binding.getRoot();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycleView.setLayoutManager(layoutManager);

        dashboardsAdapter = new DashboardsAdapter(getContext());
        list = new ArrayList<>();
        getListDashboardFromServer();
        dashboardsAdapter.setData(list);
        dashboardsAdapter.notifyDataSetChanged();
        binding.recycleView.setVisibility(View.VISIBLE);
        binding.recycleView.setAdapter(dashboardsAdapter);

        binding.backListDashboards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getFragmentManager() != null){
                    getFragmentManager().popBackStack();
                }
            }
        });

        binding.fabListDashboards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.add(new DashboardCard("Untitled"));
                dashboardsAdapter.notifyDataSetChanged();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                DashboardFragment dashboardFragment = new DashboardFragment(new DashboardCard("Untitled"));
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.list_dashboards,dashboardFragment).addToBackStack(null).commit();
            }
        });

        return view;
    }


    private void getListDashboardFromServer() {
        list.add(new DashboardCard("Đồ án môn học"));
        list.add(new DashboardCard("Đồ án môn học 1"));
        list.add(new DashboardCard("Đồ án môn học 2"));
        list.add(new DashboardCard("Đồ án môn học 3"));
        list.add(new DashboardCard("Đồ án môn học 4"));
    }
}