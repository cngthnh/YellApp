package com.yellion.yellapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.moshi.Moshi;
import com.yellion.yellapp.adapters.TransactionsAdapter;
import com.yellion.yellapp.databinding.FragmentBudgetsBinding;
import com.yellion.yellapp.databinding.FragmentCreateTransactionBinding;
import com.yellion.yellapp.databinding.FragmentTransactionCategoryBinding;
import com.yellion.yellapp.models.BudgetCard;
import com.yellion.yellapp.models.ErrorMessage;
import com.yellion.yellapp.models.TransactionCard;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetsFragment extends Fragment {
    static FragmentBudgetsBinding binding;
    static BudgetCard budgetCard;
    static TransactionsAdapter transactionsAdapter = null;
    static List<TransactionCard> listTransaction;
    SessionManager sessionManager;
    static CreateTransactionFragment createTransactionFragment;
    ApiService service;

    public BudgetsFragment(BudgetCard budgetCard, SessionManager sessionManager) {
        this.budgetCard = budgetCard;
        this.sessionManager = sessionManager;
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
        binding.idBalance.setText(String.valueOf(budgetCard.getBalance()));
        binding.idCreateDate.setText(budgetCard.created_at);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewTransaction.setLayoutManager(layoutManager);

        transactionsAdapter = new TransactionsAdapter(getContext());
        listTransaction = new ArrayList<>();
        getListTransactionsFromServer();
        transactionsAdapter.setData(listTransaction);
        transactionsAdapter.notifyDataSetChanged();
        binding.recyclerViewTransaction.setVisibility(View.VISIBLE);
        binding.recyclerViewTransaction.setAdapter(transactionsAdapter);
        binding.backListBudgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null)
                {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        binding.addTransactionBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                createTransactionFragment = new CreateTransactionFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,createTransactionFragment).addToBackStack(null).commit();
            }
        });
        binding.idBtnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetStatisticIncomeFragment budgetStatisticIncomeFragment = new BudgetStatisticIncomeFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,budgetStatisticIncomeFragment).commit();

            }
        });
        return view;
    }


    private void getListTransactionsFromServer() {

        if (budgetCard.getTransactionsList() != null) {
            List<String> listId = budgetCard.getTransactionsList();
            for (int i = 0; i < listId.size(); ++i)
                getTransactionFromServer(listId.get(i));
        }
    }

    private void getTransactionFromServer(String id) {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<TransactionCard> call;
        call = service.getTransaction(id);

        call.enqueue(new Callback<TransactionCard>() {
            @Override
            public void onResponse(Call<TransactionCard> call, Response<TransactionCard> response) {
                Log.w("YellGetTransaction", "onResponse: " + response);
                if (response.isSuccessful()) {
                    listTransaction.add((response.body()));
                    transactionsAdapter.notifyDataSetChanged();
                } else {
                    ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                    Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TransactionCard> call, Throwable t) {
                Log.w("YellTransactionFragment", "onFailure: " + t.getMessage());
            }
        });
    }


    //Create New Transaction in this Budget
    public static class CreateTransactionFragment extends Fragment {
        static FragmentCreateTransactionBinding binding_ts;
        SessionManager sessionManager;
        ApiService service;
        Moshi moshi = new Moshi.Builder().build();
        BudgetCard budgetCard;
        TransactionCard transactionCard;
        static String category="Ăn uống";

        public CreateTransactionFragment(BudgetCard budgetCard, SessionManager sessionManager) {
            this.budgetCard = budgetCard;
            this.sessionManager = sessionManager;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            binding_ts = FragmentCreateTransactionBinding.inflate(inflater, container, false);
            View view = binding_ts.getRoot();
            binding_ts.btnSaveTs.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    transactionCard = new TransactionCard();

                    String InOutCome;
                    RadioGroup radioGroup;
                    RadioButton radioButton;
                    radioGroup = (RadioGroup) view.findViewById(R.id.radio_category_ts);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) view.findViewById(selectedId);
                    if(radioButton==null)  binding_ts.btnSaveTs.setEnabled(false);
                    InOutCome=radioButton.getText().toString();
                    switch (InOutCome){
                        case "Thu nhập": transactionCard.setType(1);
                        case "Chi tiêu": transactionCard.setType(0);

                    }
                    transactionCard.setContent(binding_ts.addContentTs.getText().toString());
                    transactionCard.setBudget_id(budgetCard.getId());
                    transactionCard.setAmount(Integer.parseInt(binding_ts.addAmountTs.getText().toString()));
                    transactionCard.setPurpose(category);

                    addTransactionToServer(transactionCard);
                    binding_ts.addTransactionFragment.setVisibility(View.GONE);
                    transactionsAdapter.notifyDataSetChanged();

                    if (getActivity() != null)
                        getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            binding_ts.categoryTsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionCategoryFragment transactionCategoryFragment = new TransactionCategoryFragment();
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,transactionCategoryFragment).commit();
                }
            });
            return view;
        }

        private void addTransactionToServer(TransactionCard transactionCard) {
            service = Client.createServiceWithAuth(ApiService.class, sessionManager);
            Call<TransactionCard> call;
            String transID;

            String json = moshi.adapter(TransactionCard.class).toJson(transactionCard);
            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);
            call = service.addTransaction(requestBody);
            call.enqueue(new Callback<TransactionCard>() {
                @Override
                public void onResponse(Call<TransactionCard> call, Response<TransactionCard> response) {
                    Log.w("YellCreateTransaction", "onResponse: " + response);
                    if (response.isSuccessful()) {
                        //Toast.makeText(getActivity(), "Tạo thành công!", Toast.LENGTH_LONG).show();
                    } else {
                        {
                            ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                            Toast.makeText(getActivity(), "Tạo thất bại! " + apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }
                @Override
                public void onFailure(Call<TransactionCard> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi khi kết nối với server", Toast.LENGTH_LONG).show();
                }
            });
        }

        public static class TransactionCategoryFragment extends Fragment {

            FragmentTransactionCategoryBinding binding_ct;
            private RadioGroup radioGroup;
            private RadioButton radioButton;
            int idBtnSelected;

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

            }


            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                binding_ct = FragmentTransactionCategoryBinding.inflate(inflater, container,false);
                View view = binding_ct.getRoot();
                radioGroup = binding_ct.radioGroupCategory;
                binding_ct.btnSaveCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get selected radio button from radioGroup
                        idBtnSelected = radioGroup.getCheckedRadioButtonId();
                        View view=binding_ct.getRoot();
                        radioButton = (RadioButton) view.findViewById(idBtnSelected);
                        category=radioButton.getText().toString();
                        binding_ts.categoryTs.setText("xin chào");
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,createTransactionFragment).commit();
                    }
                });

                return view;
            }
        }
    }
}