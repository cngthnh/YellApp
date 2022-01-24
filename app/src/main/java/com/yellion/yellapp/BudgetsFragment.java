package com.yellion.yellapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
    static List<TransactionCard> list;
    SessionManager sessionManager;
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
                if (getActivity() != null)
                {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        binding.addTransactionBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBundle("budgetCard", savedInstanceState);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                CreateTransactionFragment createTransactionFragment = new CreateTransactionFragment();
                createTransactionFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.list_budgets, createTransactionFragment).addToBackStack(null).commit();
            }
        });
        binding.idBtnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.budgets_fragment, new BudgetStatisticIncomeFragment(budgetCard)).addToBackStack(null);
                transaction.commit();


            }
        });
        return view;
    }


    private void getListTransactionsFromServer() {
        list.add(new TransactionCard("thuy", 1, 1000000000, "Salary"));
        list.add(new TransactionCard("thien", 1, 1000000000, "Salary"));
        list.add(new TransactionCard("thu", 1, 1000000000, "Salary"));
        list.add(new TransactionCard("du", 1, 1000000000, "Salary"));
        list.add(new TransactionCard("h", 1, 1000000000, "Salary"));


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
                    list.add((response.body()));
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
        FragmentCreateTransactionBinding binding_ts;
        SessionManager sessionManager;
        ApiService service;
        Moshi moshi = new Moshi.Builder().build();
        static String category="other";

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
            binding_ts = FragmentCreateTransactionBinding.inflate(inflater, container, false);
            View view = binding_ts.getRoot();
            sessionManager = SessionManager.getInstance(getActivity().
                    getSharedPreferences(getResources().getString(R.string.yell_sp), Context.MODE_PRIVATE));
            binding_ts.btnSaveTs.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
//                    binding.budgetsFragment.setVisibility(View.INVISIBLE);
                    //Lấy data từ View nữa-cả Radio Button
                    String InOutCome;
                    RadioGroup radioGroup;
                    RadioButton radioButton;
                    radioGroup = (RadioGroup) view.findViewById(R.id.radio_category_ts);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    radioButton = (RadioButton) view.findViewById(selectedId);
                    InOutCome=radioButton.getText().toString();
                    //
                    TransactionCard transactionCard = new TransactionCard(InOutCome, 1, 1, category);
//                    "category",java.util.Calendar.getInstance().getTime().toString(),Integer.parseInt(binding_ts.addAmountTs.getText().toString()),binding_ts.categoryTs.getText().toString()
                    list.add(transactionCard);
                    createTransactionFromServer(transactionCard);
                    transactionsAdapter.setData(list);
                    transactionsAdapter.notifyDataSetChanged();
                    binding.recyclerViewTransaction.setAdapter(transactionsAdapter);
                    binding_ts.addTransactionFragment.setVisibility(View.GONE);
//                    binding.budgetsFragment.setVisibility(View.VISIBLE);
                }
            });
            binding_ts.categoryTsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionCategoryFragment transactionCategoryFragment = new TransactionCategoryFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.add_transaction_fragment,transactionCategoryFragment).addToBackStack(null);
                    transaction.commit();
                }
            });
            return view;
        }

        private void createTransactionFromServer(TransactionCard transactionCard) {
            service = Client.createServiceWithAuth(ApiService.class, sessionManager);
            Call<TransactionCard> call;

            String json = moshi.adapter(TransactionCard.class).toJson(transactionCard);
            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);
            call = service.addTransaction(requestBody);
            call.enqueue(new Callback<TransactionCard>() {
                @Override
                public void onResponse(Call<TransactionCard> call, Response<TransactionCard> response) {
                    Log.w("YellCreateTransaction", "onResponse: " + response);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Tạo thành công!", Toast.LENGTH_LONG).show();

                    } else {
                        {
                            ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                            Toast.makeText(getContext(), "Tạo thất bại! " + apiError.getMessage(), Toast.LENGTH_LONG).show();
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
            private Button btnSave;
            int idBtnSelected;

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addListenerOnButton();
            }

            private void addListenerOnButton() {
                radioGroup = binding_ct.radioGroupCategory;
                btnSave = binding_ct.btnSaveCategory;

                btnSave.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // get selected radio button from radioGroup
                        idBtnSelected = radioGroup.getCheckedRadioButtonId();
                        View view=binding_ct.getRoot();
                        radioButton = (RadioButton) view.findViewById(idBtnSelected);
                        category=radioButton.getText().toString();
                    }
                });
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                binding_ct = FragmentTransactionCategoryBinding.inflate(inflater, container,false);
                View view = binding.getRoot();
                return view;
            }
        }
    }
}