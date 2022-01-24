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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.moshi.Moshi;
import com.yellion.yellapp.adapters.NotificationAdapter;
import com.yellion.yellapp.adapters.UsersDetailAdapter;
import com.yellion.yellapp.databinding.FragmentHomeBinding;
import com.yellion.yellapp.databinding.FragmentListDashboardsBinding;
import com.yellion.yellapp.databinding.FragmentNotificationBinding;
import com.yellion.yellapp.models.DashboardCard;
import com.yellion.yellapp.models.DashboardPermission;
import com.yellion.yellapp.models.InfoMessage;
import com.yellion.yellapp.models.Notification;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {
    FragmentNotificationBinding binding;
    SessionManager sessionManager;
    ApiService service;
    List<Notification> listNotification;
    NotificationAdapter notificationAdapter;
    Moshi moshi = new Moshi.Builder().build();


    public NotificationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container,false);
        View view = binding.getRoot();

        binding.backNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() != null){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycleView.setLayoutManager(layoutManager);

        sessionManager = SessionManager.getInstance(getActivity().
                getSharedPreferences(getResources().getString(R.string.yell_sp), Context.MODE_PRIVATE));

        notificationAdapter = new NotificationAdapter(getContext(), sessionManager);

        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<List<Notification>> call;

        call = service.getNotification(null);
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                Log.w("YellGetNotification", "onResponse: " + response);
                if (response.isSuccessful()) {
                    listNotification = response.body();
                    notificationAdapter.setData(listNotification);
                    notificationAdapter.notifyDataSetChanged();
                    binding.recycleView.setVisibility(View.VISIBLE);
                    binding.recycleView.setAdapter(notificationAdapter);

                }
            }
            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Log.w("YellGetNotification", "onFailure: " + t.getMessage() );
            }
        });

        return view;
    }
}
