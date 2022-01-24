package com.yellion.yellapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yellion.yellapp.adapters.TransactionsAdapter;
import com.yellion.yellapp.databinding.FragmentBudgetStatisticIncomeBinding;
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

public class BudgetStatisticIncomeFragment extends Fragment {
    FragmentBudgetStatisticIncomeBinding binding;
    TransactionsAdapter transactionsAdapter = null;
    BudgetCard budgetCard;
    List<TransactionCard> list;
    ApiService service;
    SessionManager sessionManager;
    public BudgetStatisticIncomeFragment(BudgetCard budgetCard) {
        this.budgetCard=budgetCard;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBudgetStatisticIncomeBinding.inflate(inflater, container, false );
        View view = binding.getRoot();

    /*    binding.budgetName.setText(budgetCard.getName());
        binding.idBalance.setText(budgetCard.getBalance().toString());
        binding.idCreateDate.setText(budgetCard.getCreated_at());
        binding.threshold.setText(budgetCard.getThreshold().toString());
        binding.balance2.setText(budgetCard.getBalance().toString());
        int progress = budgetCard.getBalance()/budgetCard.getThreshold()*100;
        binding.circularProgressbar.setProgress(progress);
        binding.tv.setText(progress+'%');*/

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getFragmentManager() != null){
                    getFragmentManager().popBackStack();
                }
            }
        });
        binding.tnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.budgetIncome, new BudgetStatisticOutcomeFragment(budgetCard)).addToBackStack(null);
                transaction.commit();
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycleViewIncome.setLayoutManager(layoutManager);

        transactionsAdapter = new TransactionsAdapter(getContext());
        list = new ArrayList<>();
        getListTransactionsFromServer();
        transactionsAdapter.setData(list);
        transactionsAdapter.notifyDataSetChanged();
        binding.recycleViewIncome.setVisibility(View.VISIBLE);
        binding.recycleViewIncome.setAdapter(transactionsAdapter);


        return view;
    }

    private void getListTransactionsFromServer() {
        List<String> listID = budgetCard.getTransactionsList();
        for (int i = 0; i < listID.size(); ++i)
        {
            service = Client.createServiceWithAuth(ApiService.class, sessionManager);
            Call<TransactionCard> call;

            call = service.getTransaction(listID.get(i));
            call.enqueue(new Callback<TransactionCard>() {
                @Override
                public void onResponse(Call<TransactionCard> call, Response<TransactionCard> response) {
                    Log.w("GetTransaction", "onResponse: " + response);
                    if (response.isSuccessful()) {
                        if (response.body().getAmount() >= 0)
                           list.add(response.body());
                        transactionsAdapter.notifyDataSetChanged();
                    } else {
                        ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                        Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<TransactionCard> call, Throwable t) {
                    Log.w("YellInComeFragment", "onFailure: " + t.getMessage() );
                }
            });
        }
    }

}