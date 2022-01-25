package com.yellion.yellapp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
    OnBackPressedCallback pressedCallback;
    SessionManager sessionManager;
    long income;
    public BudgetStatisticIncomeFragment(BudgetCard budgetCard, SessionManager sessionManager) {
        this.budgetCard=budgetCard;
        this.sessionManager = sessionManager;
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
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("LIST_BUDGET");
                    if(fragment == null)
                        getActivity().getSupportFragmentManager().popBackStack("HOME", 0);
                    else
                        getActivity().getSupportFragmentManager().popBackStack("LIST_BUDGET", 0);
                }
                this.setEnabled(false);
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBudgetStatisticIncomeBinding.inflate(inflater, container, false );
        View view = binding.getRoot();
        requireActivity().getOnBackPressedDispatcher().addCallback(pressedCallback);

        binding.budgetName.setText(budgetCard.getName());
        binding.idBalance.setText(String.valueOf(budgetCard.getBalance()));
        String createDate = budgetCard.getCreated_at();
        int index = createDate.indexOf("T");
        createDate = createDate.substring(0, index);
        binding.idCreateDate.setText(createDate);
        binding.threshold.setText(String.valueOf(budgetCard.getThreshold()));
        binding.balance2.setText(String.valueOf(budgetCard.getBalance()));
        int progress = (int)(budgetCard.getBalance()/budgetCard.getThreshold()*100);
        if (progress > 100) progress = 100;
        binding.circularProgressbar.setProgress(progress);
        binding.tv.setText(String.valueOf(progress)+"%");

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requireActivity().onBackPressed();
            }
        });
        binding.tnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressedCallback.setEnabled(false);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetStatisticOutcomeFragment budgetStatisticOutcomeFragment = new BudgetStatisticOutcomeFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,budgetStatisticOutcomeFragment).addToBackStack(null).commit();

            }
        });

        binding.btnLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressedCallback.setEnabled(false);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetsFragment budgetsFragment = new BudgetsFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,budgetsFragment).addToBackStack(null).commit();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycleViewIncome.setLayoutManager(layoutManager);

        transactionsAdapter = new TransactionsAdapter(getContext(), sessionManager);
        list = new ArrayList<>();
        getListTransactionsFromServer();
        transactionsAdapter.setData(list);
        transactionsAdapter.notifyDataSetChanged();
        binding.recycleViewIncome.setVisibility(View.VISIBLE);
        binding.recycleViewIncome.setAdapter(transactionsAdapter);
        binding.balance2.setText(String.valueOf(income));

        return view;
    }

    private void getListTransactionsFromServer() {
        if (budgetCard.getTransactionsList() != null) {
            {
                List<TransactionCard> listID = budgetCard.getTransactionsList();
                if (listID.size() != 0)
                    for (int i = 0; i < listID.size(); ++i)
                    if (listID.get(i).getAmount()>0)
                    {
                        income += listID.get(i).getAmount();

                        list.add(listID.get(i));
                        transactionsAdapter.notifyDataSetChanged();
                    }
            }
        }
    }

}