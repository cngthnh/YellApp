package com.yellion.yellapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.moshi.Moshi;
import com.yellion.yellapp.databinding.FragmentAccountBinding;
import com.yellion.yellapp.databinding.FragmentCreateBudgetBinding;
import com.yellion.yellapp.models.BudgetCard;
import com.yellion.yellapp.models.BudgetId;
import com.yellion.yellapp.models.ErrorMessage;
import com.yellion.yellapp.models.InfoMessage;
import com.yellion.yellapp.models.TokenPair;
import com.yellion.yellapp.models.UserAccount;
import com.yellion.yellapp.models.UserCredentials;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateBudget extends Fragment {
    FragmentCreateBudgetBinding binding;
    ApiService service;
    SessionManager sessionManager;
    Moshi moshi = new Moshi.Builder().build();
    BudgetCard budgetCard;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateBudgetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        budgetCard = new BudgetCard();

        sessionManager = SessionManager.getInstance(getActivity().
                getSharedPreferences(getResources().getString(R.string.yell_sp), Context.MODE_PRIVATE));

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cảnh báo điền đầy đủ
                budgetCard.setName(binding.budgetNameInput.getText().toString());
                budgetCard.setBalance(Integer.parseInt(binding.balanceInput.getText().toString()));
                budgetCard.setThreshold(Integer.parseInt(binding.thresholdInput.getText().toString()));

                addBudgetToServer();

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.remove(CreateBudget.this);
                transaction.replace(R.id.create_budget_fragment, new ListBudgetsFragment()).addToBackStack(null);
                transaction.commit();
            }
        });

        binding.Type0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.Type0.setBackgroundColor(Color.LTGRAY);
                binding.Type1.setBackgroundColor(Color.WHITE);
                budgetCard.setType(0);
            }
        });

        binding.Type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.Type1.setBackgroundColor(Color.LTGRAY);
                binding.Type0.setBackgroundColor(Color.WHITE);
                budgetCard.setType(1);

            }
        });
        return view;
    }

    private void addBudgetToServer()
    {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<BudgetCard> call;

        String json = moshi.adapter(BudgetCard.class).toJson(budgetCard);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);
        Log.w("BudgetCreate", budgetCard.getName() + budgetCard.getBalance());

        call = service.createBudget(requestBody);
        call.enqueue(new Callback<BudgetCard>() {
            @Override
            public void onResponse(Call<BudgetCard> call, Response<BudgetCard> response) {

                Log.w("BudgetCreate", "onResponse: " + response);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),"Tạo thành công!", Toast.LENGTH_LONG).show();

                } else {
                    if (response.code() == 401) {
                        ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BudgetCard> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi kết nối với server", Toast.LENGTH_LONG).show();
            }
        });


    }


}