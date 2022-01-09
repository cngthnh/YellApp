package com.yellion.yellapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yellion.yellapp.databinding.FragmentBudgetStatisticOutcomeBinding;
import com.yellion.yellapp.databinding.FragmentBudgetsBinding;
import com.yellion.yellapp.models.BudgetCard;

public class BudgetStatisticOutcomeFragment extends Fragment {

    FragmentBudgetStatisticOutcomeBinding binding;
    BudgetCard budgetCard;
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
        binding.idThreshold.setText(budgetCard.getThreshold());
        binding.idCreateDate.setText(budgetCard.getStart_time());

        return inflater.inflate(R.layout.fragment_budget_statistic_outcome, container, false);
    }
}