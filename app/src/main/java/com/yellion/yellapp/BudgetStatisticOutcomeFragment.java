package com.yellion.yellapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yellion.yellapp.adapters.TransactionsAdapter;
import com.yellion.yellapp.databinding.FragmentBudgetStatisticOutcomeBinding;
import com.yellion.yellapp.models.BudgetCard;
import com.yellion.yellapp.models.ErrorMessage;
import com.yellion.yellapp.models.TransactionCard;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetStatisticOutcomeFragment extends Fragment {

    FragmentBudgetStatisticOutcomeBinding binding;
    BudgetCard budgetCard;
    TransactionsAdapter transactionsAdapter = null;

    List<TransactionCard> list;
    ApiService service;
    SessionManager sessionManager;

    public BudgetStatisticOutcomeFragment(BudgetCard budgetCard, SessionManager sessionManager) {
        this.budgetCard=budgetCard;
        this.sessionManager = sessionManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBudgetStatisticOutcomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        binding.budgetName.setText(budgetCard.getName());
        binding.idBalance.setText(String.valueOf(budgetCard.getBalance()));
        binding.idCreateDate.setText(budgetCard.getCreated_at());
        binding.threshold.setText(String.valueOf(budgetCard.getThreshold()));
        binding.balance2.setText(String.valueOf(budgetCard.getBalance()));
        int progress = budgetCard.getBalance()/budgetCard.getThreshold()*100;

        binding.circularProgressbar.setProgress(progress);
        binding.tv.setText(String.valueOf(progress)+'%');

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        binding.tn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetStatisticIncomeFragment budgetStatisticIncomeFragment = new BudgetStatisticIncomeFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,budgetStatisticIncomeFragment).addToBackStack(null).commit();
            }
        });

        binding.btnLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetsFragment budgetsFragment = new BudgetsFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,budgetsFragment).addToBackStack(null).commit();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycleViewOutcome.setLayoutManager(layoutManager);

        transactionsAdapter = new TransactionsAdapter(getContext());
        list = new ArrayList<>();
        getListTransactionsFromServer();
        transactionsAdapter.setData(list);
        transactionsAdapter.notifyDataSetChanged();
        binding.recycleViewOutcome.setVisibility(View.VISIBLE);
        binding.recycleViewOutcome.setAdapter(transactionsAdapter);

        return view;
    }

    private void getListTransactionsFromServer() {
        if (budgetCard.getTransactionsList() != null) {
            {
                List<String> listID = budgetCard.getTransactionsList();
                if (listID.size() != 0)
                    for (int i = 0; i < listID.size(); ++i) {
                        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
                        Call<TransactionCard> call;

                        call = service.getTransaction(listID.get(i));
                        call.enqueue(new Callback<TransactionCard>() {
                            @Override
                            public void onResponse(Call<TransactionCard> call, Response<TransactionCard> response) {
                                Log.w("GetTransaction", "onResponse: " + response);
                                if (response.isSuccessful()) {
                                    if (response.body().getType() < 0)
                                        list.add(response.body());
                                    transactionsAdapter.notifyDataSetChanged();
                                } else {
                                    ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                                    Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<TransactionCard> call, Throwable t) {
                                Log.w("YellInComeFragment", "onFailure: " + t.getMessage());
                            }
                        });
                    }
            }
        }
    }
}