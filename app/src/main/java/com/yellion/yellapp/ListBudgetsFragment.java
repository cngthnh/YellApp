package com.yellion.yellapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.moshi.Moshi;
import com.yellion.yellapp.adapters.BudgetsAdapter;
import com.yellion.yellapp.databinding.FragmentListBudgetsBinding;
import com.yellion.yellapp.models.BudgetCard;
import com.yellion.yellapp.models.ErrorMessage;
import com.yellion.yellapp.models.UserAccount;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListBudgetsFragment extends Fragment {
    FragmentListBudgetsBinding binding;
    BudgetsAdapter budgetsAdapter = null;
    List<BudgetCard> list;
    ApiService service;
    SessionManager sessionManager;
    OnBackPressedCallback pressedCallback;
    List<String> listIdBudget;
    Moshi moshi = new Moshi.Builder().build();
    public ListBudgetsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getActivity() != null)
                {
                    this.setEnabled(false);
                    getActivity().getSupportFragmentManager().popBackStack("HOME", 0);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sessionManager = SessionManager.getInstance(getActivity().
                getSharedPreferences(getResources().getString(R.string.yell_sp), Context.MODE_PRIVATE));
        // Inflate the layout for this fragment
        binding = FragmentListBudgetsBinding.inflate(inflater, container, false );
        View view = binding.getRoot();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycleView.setLayoutManager(layoutManager);

        budgetsAdapter = new BudgetsAdapter(getContext(), sessionManager);
        list = new ArrayList<>();


        getListBudgetsFromServer();

        budgetsAdapter.setData(list);
        budgetsAdapter.notifyDataSetChanged();
        binding.recycleView.setVisibility(View.VISIBLE);
        binding.recycleView.setAdapter(budgetsAdapter);

        binding.backListBudgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getOnBackPressedDispatcher().addCallback(pressedCallback);
                requireActivity().onBackPressed();
            }
        });

        binding.fabListBudgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateBudget createBudget = new CreateBudget();

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer,createBudget)
                        .addToBackStack(null).commit();
                budgetsAdapter.notifyDataSetChanged();

            }
        });

        return view;
    }

    private void getListIdBudget() {
        listIdBudget = new ArrayList<>();
        for (int i = 0; i < list.size(); ++i)
            listIdBudget.add(list.get(i).getId());
    }


    private void getListBudgetsFromServer() {
        getListIdBudget();
        if (sessionManager.getToken()!=null) {
            service = Client.createServiceWithAuth(ApiService.class, sessionManager);
            Call<UserAccount> call;
            call = service.getUserProfile("compact");
            call.enqueue(new Callback<UserAccount>() {
                @Override
                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                    Log.w("YellBudgetGet", "onResponse: " + response);
                    if (response.isSuccessful()) {

                        getListBudget(response.body().getBudgets());

                    } else {
                        ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                        Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserAccount> call, Throwable t) {
                    Log.w("YellBudgetFromUser", "onFailure: " + t.getMessage() );
                }
            });
        }
    }
    private void getListBudget(List<String> budget){
        if (budget.size() != 0){
            for (int i = 0; i < budget.size(); ++i) {
                if (!listIdBudget.contains(budget.get(i)))
                    getBudgetFromServer(budget.get(i));
            }}

    }

    private void getBudgetFromServer(String id) {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<BudgetCard> call;

        call = service.getBudget(id, "full");
        call.enqueue(new Callback<BudgetCard>() {
            @Override
            public void onResponse(Call<BudgetCard> call, Response<BudgetCard> response) {
                Log.w("GetBudget", "onResponse: " + response);
                if (response.isSuccessful()) {

                    list.add(response.body());
                    budgetsAdapter.notifyDataSetChanged();
                } else {
                    ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                    Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BudgetCard> call, Throwable t) {
                Log.w("YellBudgetFragment", "onFailure: " + t.getMessage() );
            }
        });
    }

}