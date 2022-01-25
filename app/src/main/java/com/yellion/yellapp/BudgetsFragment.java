package com.yellion.yellapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
    OnBackPressedCallback pressedCallback;
    static CreateTransactionFragment createTransactionFragment;


    public BudgetsFragment(BudgetCard budgetCard, SessionManager sessionManager) {
        this.budgetCard = budgetCard;
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
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("LIST_BUDGET");
                    if(fragment == null)
                        getActivity().getSupportFragmentManager().popBackStack("HOME", 0);
                    else
                        getActivity().getSupportFragmentManager().popBackStack("LIST_BUDGET", 0);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(pressedCallback);
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

        String createDate = budgetCard.getCreated_at();
        int index = createDate.indexOf("T");
        createDate = createDate.substring(0, index);

        binding.idCreateDate.setText(createDate);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewTransaction.setLayoutManager(layoutManager);

        transactionsAdapter = new TransactionsAdapter(getContext(),sessionManager);
        list = new ArrayList<>();
        getListTransactionsFromServer();
        transactionsAdapter.setData(list);
        transactionsAdapter.notifyDataSetChanged();
        binding.recyclerViewTransaction.setVisibility(View.VISIBLE);
        binding.recyclerViewTransaction.setAdapter(transactionsAdapter);
        binding.backListBudgets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });

        binding.addTransactionBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                CreateTransactionFragment createTransactionFragment = new CreateTransactionFragment(sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,createTransactionFragment).addToBackStack(null).commit();
            }
        });
        binding.idBtnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetStatisticIncomeFragment budgetStatisticIncomeFragment = new BudgetStatisticIncomeFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,budgetStatisticIncomeFragment).addToBackStack(null).commit();

            }
        });
        return view;
    }


    private void getListTransactionsFromServer() {
        if (budgetCard.getTransactionsList() != null) {
            List<TransactionCard> listId = budgetCard.getTransactionsList();
            for (int i = 0; i < listId.size(); ++i) {
                list.add(listId.get(i));
                transactionsAdapter.notifyDataSetChanged();
            }
        }

        binding.idBalance.setText(String.valueOf(budgetCard.getBalance()));
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
        TransactionCard transactionCard;

        static String category="other";

        public CreateTransactionFragment(SessionManager sessionManager) {
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
                    transactionCard.setType(-1);
                    String InOutCome;
                    RadioGroup radioGroup;
                    RadioButton radioButton;
                    radioGroup = (RadioGroup) view.findViewById(R.id.radio_category_ts);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId != -1){
                    radioButton = (RadioButton) view.findViewById(selectedId);
                    InOutCome=radioButton.getText().toString();
                    switch (InOutCome){
                        case "Thu nhập": transactionCard.setType(1);
                        case "Chi tiêu": transactionCard.setType(0);
                    }}

                    if (binding_ts.addContentTs.getText().toString().equals("") ||binding_ts.addAmountTs.getText().toString().equals("")
                            || transactionCard.getType() == -1 || category.equals("other"))
                        Toast.makeText(getContext(),"Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_LONG).show();
                    else {
                        transactionCard.setContent(binding_ts.addContentTs.getText().toString());
                        transactionCard.setBudget_id(budgetCard.getId());
                        if (transactionCard.getType() == 1)
                            transactionCard.setAmount(Integer.parseInt(binding_ts.addAmountTs.getText().toString()));
                        else
                            transactionCard.setAmount(-1 * Integer.parseInt(binding_ts.addAmountTs.getText().toString()));

                        transactionCard.setPurpose(category);

                        addTransactionToServer(transactionCard);

                        //binding_ts.addTransactionFragment.setVisibility(View.GONE);
                        transactionsAdapter.notifyDataSetChanged();
                        if (getActivity() != null)
                            getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
            binding_ts.categoryTsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransactionCategoryFragment transactionCategoryFragment = new TransactionCategoryFragment();
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,transactionCategoryFragment).addToBackStack(null).commit();
                }
            });
            return view;
        }
        private void getBudget() {
            service = Client.createServiceWithAuth(ApiService.class, sessionManager);
            Call<BudgetCard> call;

            call = service.getBudget(budgetCard.getId(), "full");
            call.enqueue(new Callback<BudgetCard>() {
                @Override
                public void onResponse(Call<BudgetCard> call, Response<BudgetCard> response) {
                    Log.w("GetBudget", "onResponse: " + response);
                    if (response.isSuccessful()) {
                        budgetCard.setBalance(response.body().getBalance());
                        budgetCard.setTransactionsList(response.body().getTransactionsList());

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
                        getBudget();
                    } else {
                        {
                            ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                            //  Toast.makeText(getActivity(), "Tạo thất bại! " + apiError.getMessage(), Toast.LENGTH_LONG).show();
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
                binding_ct = FragmentTransactionCategoryBinding.inflate(inflater, container, false);
                View view = binding_ct.getRoot();
                radioGroup = binding_ct.radioGroupCategory;
                binding_ct.btnSaveCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get selected radio button from radioGroup
                        idBtnSelected = radioGroup.getCheckedRadioButtonId();
                        View view = binding_ct.getRoot();
                        radioButton = (RadioButton) view.findViewById(idBtnSelected);
                        category = radioButton.getText().toString();
                        binding_ts.categoryTs.setText("xin chào");

                        if (getActivity() != null)
                            getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

                return view;
            }
        }
    }
}