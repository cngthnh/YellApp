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
                    //confirm(listNotification.get(0));

                }
            }
            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Log.w("YellGetNotification", "onFailure: " + t.getMessage() );
            }
        });

        if(listNotification != null){
            Call<InfoMessage> call1;

            String json = moshi.adapter(Notification.class).toJson(listNotification.get(0));
            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);

            call1 = service.confirmInvited(requestBody);
            call1.enqueue(new Callback<InfoMessage>() {
                @Override
                public void onResponse(Call<InfoMessage> call, Response<InfoMessage> response) {
                    Log.w("YellConfirm", "onResponse: " + response);
                }

                @Override
                public void onFailure(Call<InfoMessage> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi khi kết nối với server", Toast.LENGTH_LONG).show();
                }
            });
        }

        return view;
    }


}
