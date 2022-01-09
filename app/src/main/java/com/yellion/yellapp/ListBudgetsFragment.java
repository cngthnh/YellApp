package com.yellion.yellapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yellion.yellapp.adapters.BudgetsAdapter;
import com.yellion.yellapp.databinding.FragmentListBudgetsBinding;
import com.yellion.yellapp.models.BudgetCard;

import java.util.ArrayList;
import java.util.List;

public class ListBudgetsFragment extends Fragment {
    FragmentListBudgetsBinding binding;
    BudgetsAdapter budgetsAdapter = null;
    List<BudgetCard> list;
    public ListBudgetsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListBudgetsBinding.inflate(inflater, container, false );
        View view = binding.getRoot();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recycleView.setLayoutManager(layoutManager);

        budgetsAdapter = new BudgetsAdapter(getContext());
        list = new ArrayList<>();
        getListBudgetsFromServer();
        budgetsAdapter.setData(list);
        budgetsAdapter.notifyDataSetChanged();
        binding.recycleView.setVisibility(View.VISIBLE);
        binding.recycleView.setAdapter(budgetsAdapter);

        binding.backListBudgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getFragmentManager() != null){
                    getFragmentManager().popBackStack();
                }
            }
        });

        binding.fabListBudgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.add(new BudgetCard("Sổ tay chi tiêu",20000,20000,"yyyy/mm/dd","yyyy/mm/dd","yyyy/mm/dd"));
                budgetsAdapter.notifyDataSetChanged();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetsFragment budgetsFragment = new BudgetsFragment((new BudgetCard()));
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.list_budgets,budgetsFragment).addToBackStack(null).commit();
            }
        });

        return view;
    }


    private void getListBudgetsFromServer() {
        list.add(new BudgetCard("1",20000,20000,"yyyy/mm/dd","yyyy/mm/dd","yyyy/mm/dd" ));
        list.add(new BudgetCard("2",20000,20000,"yyyy/mm/dd","yyyy/mm/dd","yyyy/mm/dd" ));
        list.add(new BudgetCard("3",20000,20000,"yyyy/mm/dd","yyyy/mm/dd","yyyy/mm/dd" ));
        list.add(new BudgetCard("4",20000,20000,"yyyy/mm/dd","yyyy/mm/dd","yyyy/mm/dd" ));
        list.add(new BudgetCard("5",20000,20000,"yyyy/mm/dd","yyyy/mm/dd","yyyy/mm/dd" ));

    }
}