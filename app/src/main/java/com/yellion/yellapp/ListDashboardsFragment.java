package com.yellion.yellapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.moshi.Moshi;
import com.yellion.yellapp.adapters.DashboardsAdapter;
import com.yellion.yellapp.databinding.FragmentListDashboardsBinding;
import com.yellion.yellapp.models.DashboardCard;
import com.yellion.yellapp.models.ErrorMessage;
import com.yellion.yellapp.models.UserAccount;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;
import com.yellion.yellapp.viewmodels.DashboardViewModel;
import com.yellion.yellapp.viewmodels.DashboardViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListDashboardsFragment extends Fragment {
    FragmentListDashboardsBinding binding;
    DashboardsAdapter dashboardsAdapter = null;
    List<DashboardCard> list;
    SessionManager sessionManager;
    ApiService service;
    Moshi moshi = new Moshi.Builder().build();
    DashboardViewModel dashboardViewModel;

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

        sessionManager = SessionManager.getInstance(getActivity().
                getSharedPreferences(getResources().getString(R.string.yell_sp), Context.MODE_PRIVATE));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycleView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        getListDashboardFromServer();

        dashboardViewModel = new ViewModelProvider(this.getActivity(), new DashboardViewModelFactory(list)).get(DashboardViewModel.class);
        dashboardViewModel.getListDashboardLiveData().observe(this.getActivity(), new Observer<List<DashboardCard>>() {
            @Override
            public void onChanged(List<DashboardCard> dashboardCards) {
                dashboardsAdapter = new DashboardsAdapter(getContext(), sessionManager);
                dashboardsAdapter.setData(dashboardCards);
                binding.recycleView.setAdapter(dashboardsAdapter);
            }
        });

        /*
        dashboardsAdapter = new DashboardsAdapter(getContext());
        list = new ArrayList<>();
        getListDashboardFromServer();
        dashboardsAdapter.setData(list);
        dashboardsAdapter.notifyDataSetChanged();
        binding.recycleView.setVisibility(View.VISIBLE);
        binding.recycleView.setAdapter(dashboardsAdapter);
        */


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
                DashboardCard dashboardCard = new DashboardCard("Untitled");
                addDashboardToServer(dashboardCard);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                DashboardFragment dashboardFragment = new DashboardFragment(dashboardCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.list_dashboards,dashboardFragment).addToBackStack(null).commit();

                /*
                dashboardViewModel.getListDashboardLiveData().observe(getActivity(), new Observer<List<DashboardCard>>() {
                    @Override
                    public void onChanged(List<DashboardCard> dashboardCards) {
                        }
                });*/

            }
        });

        return view;
    }

    private void addDashboardToServer(DashboardCard dashboardCard) {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<DashboardCard> call;

        String json = moshi.adapter(DashboardCard.class).toJson(dashboardCard);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);
        call = service.addDashboard(requestBody);
        call.enqueue(new Callback<DashboardCard>() {
            @Override
            public void onResponse(Call<DashboardCard> call, Response<DashboardCard> response) {
                Log.w("YellDashboardCreate", "onResponse: " + response);
                if (response.isSuccessful()) {
                    String id = response.body().getId();
                    dashboardCard.setId(id);
                    addList(dashboardCard);
                } else {
                    if (response.code() == 401) {
                        ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }
            @Override
            public void onFailure(Call<DashboardCard> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi kết nối với server", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addList(DashboardCard dashboardCard) {
        dashboardViewModel.addDashboard(dashboardCard);
    }


    private void getListDashboardFromServer() {
        if (sessionManager.getToken()!=null) {
            service = Client.createServiceWithAuth(ApiService.class, sessionManager);
            Call<UserAccount> call;
            call = service.getUserProfile("compact");
            call.enqueue(new Callback<UserAccount>() {
                @Override
                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                    Log.w("YellDashboardGet", "onResponse: " + response);
                    if (response.isSuccessful()) {
                        List<String> dashboards = response.body().getDashboards();
                        getListDashboard(dashboards);
                    } else {
                        /*ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                        Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                        sessionManager.deleteToken();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);*/
                    }
                }

                @Override
                public void onFailure(Call<UserAccount> call, Throwable t) {
                    Log.w("YellDashboardFragment", "onFailure: " + t.getMessage() );
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
                    Log.w("YellDashboardGet", "onResponse: " + response);
                    if (response.isSuccessful()) {
                        list.add(response.body());
                        dashboardsAdapter.notifyDataSetChanged();
                    } else {
                        /*ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                        Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                        sessionManager.deleteToken();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);*/
                    }
                }

                @Override
                public void onFailure(Call<DashboardCard> call, Throwable t) {
                    Log.w("YellDashboardFragment", "onFailure: " + t.getMessage() );
                }
            });

        }
    }
}