package com.yellion.yellapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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

    public CreateBudget() {
    }

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

                if (binding.budgetNameInput.getText().toString().equals("")
                        || binding.balanceInput.getText().toString().equals("")
                        || binding.thresholdInput.getText().toString().equals(""))
                    Toast.makeText(getContext(),"Vui lòng điền đầy đủ thông tin",Toast.LENGTH_LONG).show();

                else{
                    budgetCard.setName(binding.budgetNameInput.getText().toString());
                    budgetCard.setBalance(Long.parseLong(binding.balanceInput.getText().toString()));
                    budgetCard.setThreshold(Long.parseLong(binding.thresholdInput.getText().toString()));

                    addBudgetToServer(budgetCard);

                /*    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer,new ListBudgetsFragment())
                            .addToBackStack(null).commit();*/

                    if(getActivity() != null)
                        getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        binding.Type0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.Type0.setBackgroundColor(Color.DKGRAY);
                binding.Type0.setTextColor(Color.WHITE);
                binding.Type1.setBackgroundColor(Color.WHITE);
                binding.Type1.setTextColor(Color.BLACK);

                binding.thresholdCard.setVisibility(View.VISIBLE);
                binding.textThreshold.setText("Giới hạn chi tiêu");
                budgetCard.setType(0);
            }
        });

        binding.Type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.Type1.setBackgroundColor(Color.DKGRAY);
                binding.Type1.setTextColor(Color.WHITE);
                binding.Type0.setBackgroundColor(Color.WHITE);
                binding.Type0.setTextColor(Color.BLACK);
                binding.thresholdCard.setVisibility(View.VISIBLE);
                binding.textThreshold.setText("Mục tiêu tiết kiệm");
                budgetCard.setType(1);

            }
        });
        return view;
    }

    private void addBudgetToServer(BudgetCard budgetCard)
    {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<BudgetCard> call;

        String json = moshi.adapter(BudgetCard.class).toJson(budgetCard);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);

        call = service.createBudget(requestBody);
        call.enqueue(new Callback<BudgetCard>() {
            @Override
            public void onResponse(Call<BudgetCard> call, Response<BudgetCard> response) {

                Log.w("BudgetCreate", "onResponse: " + response);
                if (response.isSuccessful()) {
                    try {
                        Toast.makeText(getContext(), "Tạo thành công", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e){
                        Log.e("sus", e.toString());
                    }


                } else {

                    ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                    //Toast.makeText(getActivity(), "Tạo thất bại: " + apiError.getMessage(), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<BudgetCard> call, Throwable t) {
                //Toast.makeText(getContext(), "Lỗi khi kết nối với server", Toast.LENGTH_LONG).show();
                Log.w("YellCreateBudget", "onFailure: " + t.getMessage());
            }
        });


    }


}