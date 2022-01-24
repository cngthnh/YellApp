package com.yellion.yellapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yellion.yellapp.adapters.TransactionsAdapter;
import com.yellion.yellapp.databinding.FragmentBudgetsBinding;
import com.yellion.yellapp.databinding.FragmentCreateTransactionBinding;
import com.yellion.yellapp.models.BudgetCard;
import com.yellion.yellapp.models.TransactionCard;

import java.util.ArrayList;
import java.util.List;

public class BudgetsFragment extends Fragment {
    static FragmentBudgetsBinding binding;
    static BudgetCard budgetCard;
    static TransactionsAdapter transactionsAdapter = null;
    static List<TransactionCard> list;

    public BudgetsFragment(BudgetCard budgetCard) {
        this.budgetCard = budgetCard;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBudgetsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.nameBg.setText(budgetCard.name);
        binding.idBalance.setText(String.format("%d", budgetCard.balance));
        binding.idCreateDate.setText(budgetCard.created_at);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewTransaction.setLayoutManager(layoutManager);

        transactionsAdapter = new TransactionsAdapter(getContext());
        list = new ArrayList<>();
        getListTransactionsFromServer();
        transactionsAdapter.setData(list);
        transactionsAdapter.notifyDataSetChanged();
        binding.recyclerViewTransaction.setVisibility(View.VISIBLE);
        binding.recyclerViewTransaction.setAdapter(transactionsAdapter);
        binding.backListBudgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        binding.addTransactionBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putBundle("budgetCard",savedInstanceState);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                CreateTransactionFragment createTransactionFragment = new CreateTransactionFragment();
                createTransactionFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.list_budgets,createTransactionFragment).addToBackStack(null).commit();
            }
        });


        return view;
    }


    private void getListTransactionsFromServer() {
        list.add(new TransactionCard("thuy", "21/01/2021", 1000000000, "Salary"));
        list.add(new TransactionCard("thien", "21/01/2021", 1000000000, "Salary"));
        list.add(new TransactionCard("thu", "21/01/2021", 1000000000, "Salary"));
        list.add(new TransactionCard("du", "21/01/2021", 1000000000, "Salary"));
        list.add(new TransactionCard("h", "21/01/2021", 1000000000, "Salary"));
    }


    //Create New Transaction in this Budget
    public static class CreateTransactionFragment extends Fragment {
        FragmentCreateTransactionBinding binding_ts;
        public CreateTransactionFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        TransactionCard transactionCard;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            binding_ts = FragmentCreateTransactionBinding.inflate(inflater, container, false );
            View view = binding_ts.getRoot();
            binding_ts.btnSaveTs.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
//                    binding.budgetsFragment.setVisibility(View.INVISIBLE);
                    //Lấy data từ View nữa-cả Radio Button
                    TransactionCard transactionCard=new TransactionCard("a","b",1,"f");
//                    "category",java.util.Calendar.getInstance().getTime().toString(),Integer.parseInt(binding_ts.addAmountTs.getText().toString()),binding_ts.categoryTs.getText().toString()
                    list.add(transactionCard);
                    transactionsAdapter.setData(list);
                    transactionsAdapter.notifyDataSetChanged();
                    binding.recyclerViewTransaction.setAdapter(transactionsAdapter);
                    binding_ts.addTransactionFragment.setVisibility(View.GONE);
//                    binding.budgetsFragment.setVisibility(View.VISIBLE);
                }
            });
            return view;
        }

    }
}