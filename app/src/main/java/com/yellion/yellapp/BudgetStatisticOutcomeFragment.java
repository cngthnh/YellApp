package com.yellion.yellapp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
    OnBackPressedCallback pressedCallback;

    public BudgetStatisticOutcomeFragment(BudgetCard budgetCard, SessionManager sessionManager) {
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
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBudgetStatisticOutcomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();


        binding.budgetName.setText(budgetCard.getName());
        binding.idBalance.setText(String.valueOf(budgetCard.getBalance()));
        String createDate = budgetCard.getCreated_at();
        int index = createDate.indexOf("T");
        createDate = createDate.substring(0, index);
        binding.idCreateDate.setText(createDate);
        binding.threshold.setText(String.valueOf(budgetCard.getThreshold()));
        binding.balance2.setText(String.valueOf(budgetCard.getBalance()));
        int progress = budgetCard.getBalance()/budgetCard.getThreshold()*100;

        binding.circularProgressbar.setProgress(progress);
        binding.tv.setText(String.valueOf(progress)+'%');

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getOnBackPressedDispatcher().addCallback(pressedCallback);
                requireActivity().onBackPressed();
            }
        });
        binding.tn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetStatisticIncomeFragment budgetStatisticIncomeFragment = new BudgetStatisticIncomeFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,budgetStatisticIncomeFragment).commit();
            }
        });

        binding.btnLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetsFragment budgetsFragment = new BudgetsFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,budgetsFragment).commit();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycleViewOutcome.setLayoutManager(layoutManager);

        transactionsAdapter = new TransactionsAdapter(getContext(),sessionManager);
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
                List<TransactionCard> listID = budgetCard.getTransactionsList();
                if (listID.size() != 0)
                    for (int i = 0; i < listID.size(); ++i)
                    {
                        list.add(listID.get(i));
                        transactionsAdapter.notifyDataSetChanged();
                    }
            }
        }
    }
}