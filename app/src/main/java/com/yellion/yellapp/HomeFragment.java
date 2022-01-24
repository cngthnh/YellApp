package com.yellion.yellapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yellion.yellapp.adapters.DashboardsHomeAdapter;
import com.yellion.yellapp.databinding.FragmentHomeBinding;
import com.yellion.yellapp.models.DashboardCard;
import com.yellion.yellapp.models.UserAccount;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    DashboardsHomeAdapter dashboardsHomeAdapter = null;
    List<DashboardCard> list;
    SessionManager sessionManager;
    ApiService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container,false);
        View view = binding.getRoot();

        sessionManager = SessionManager.getInstance(getActivity().
                getSharedPreferences(getResources().getString(R.string.yell_sp), Context.MODE_PRIVATE));


        binding.viewAllBudgetsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                ListBudgetsFragment listBudgetsFragment = new ListBudgetsFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment,listBudgetsFragment).addToBackStack(null).commit();
                binding.highlightCard.setVisibility(View.GONE);
                ListDashboardsFragment dashboardsFragment = new ListDashboardsFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,dashboardsFragment, "LIST_DASHBOARD").addToBackStack(null).commit();
            }
        });

        binding.notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                NotificationFragment notificationFragment = new NotificationFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,notificationFragment, "NOTIFICATION").addToBackStack(null).commit();
            }
        });
        binding.viewAllDashboardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                ListDashboardsFragment dashboardsFragment = new ListDashboardsFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,dashboardsFragment, "LIST_DASHBOARD").addToBackStack(null).commit();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),  LinearLayoutManager.HORIZONTAL, false);
        binding.dashboardPreviewList.setLayoutManager(layoutManager);

        dashboardsHomeAdapter = new DashboardsHomeAdapter(getContext(), sessionManager);
        list = new ArrayList<>();
        getListDashboardFromServer();
        dashboardsHomeAdapter.setData(list);
        dashboardsHomeAdapter.notifyDataSetChanged();
        binding.dashboardPreviewList.setVisibility(View.VISIBLE);
        binding.dashboardPreviewList.setAdapter(dashboardsHomeAdapter);



        return view;
    }

    private void getListDashboardFromServer() {
        if (sessionManager.getToken()!=null) {
            service = Client.createServiceWithAuth(ApiService.class, sessionManager);
            Call<UserAccount> call;
            call = service.getUserProfile("compact");
            call.enqueue(new Callback<UserAccount>() {
                @Override
                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                    Log.w("YellGetListDashboard", "onResponse: " + response);
                    if (response.isSuccessful()) {
                        List<String> dashboards = response.body().getDashboards();
                        getListDashboard(dashboards);
                    }
                }
                @Override
                public void onFailure(Call<UserAccount> call, Throwable t) {
                    Log.w("YellGetListDashboard", "onFailure: " + t.getMessage() );
                }
            });
        }
    }

    private void getListDashboard(List<String> dashboards) {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<DashboardCard> call;

        for (int i = 0; i < dashboards.size(); i++)
        {
            call = service.getDashboard(dashboards.get(i), "full");
            call.enqueue(new Callback<DashboardCard>() {
                @Override
                public void onResponse(Call<DashboardCard> call, Response<DashboardCard> response) {
                    Log.w("YellGetDashboard", "onResponse: " + response);
                    if (response.isSuccessful()) {
                        list.add(response.body());
                        dashboardsHomeAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onFailure(Call<DashboardCard> call, Throwable t) {
                    Log.w("YellGetDashboard", "onFailure: " + t.getMessage() );
                }
            });

        }
    }
}