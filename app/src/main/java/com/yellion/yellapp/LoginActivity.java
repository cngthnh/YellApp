package com.yellion.yellapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.common.hash.Hashing;
import com.squareup.moshi.Moshi;
import com.yellion.yellapp.databinding.ActivityLoginBinding;
import com.yellion.yellapp.models.ErrorMessage;
import com.yellion.yellapp.models.TokenPair;
import com.yellion.yellapp.models.UserCredentials;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.GlobalStatus;
import com.yellion.yellapp.utils.TokenManager;

import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ApiService service;
    TokenManager tokenManager;
    AwesomeValidation validator;
    Call<TokenPair> call;
    private ActivityLoginBinding binding;
    Moshi moshi = new Moshi.Builder().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        binding.goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });

        binding.guestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalStatus.getInstance().setGuestMode(true);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        service = Client.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences(getResources().getString(R.string.yell_sp), MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

        setupInputRules();

        if (tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void showLoading(){
        TransitionManager.beginDelayedTransition(binding.container);
        binding.formContainer.setVisibility(View.GONE);
        binding.loader.setVisibility(View.VISIBLE);
    }

    private void showForm(){
        TransitionManager.beginDelayedTransition(binding.container);
        binding.formContainer.setVisibility(View.VISIBLE);
        binding.loader.setVisibility(View.GONE);
    }


    void login() {

        String username = binding.usernameInput.getEditText().getText().toString();
        String password = binding.passwordInput.getEditText().getText().toString();

        String hash = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();

        UserCredentials userCredentials = new UserCredentials(username, hash);

        String jsonCredentials = moshi.adapter(UserCredentials.class).toJson(userCredentials);

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), jsonCredentials);

        binding.usernameInput.setError(null);
        binding.passwordInput.setError(null);

        validator.clear();

        if (validator.validate()) {
            showLoading();
            try {
                call = service.login(requestBody);
            }
            catch (Exception e)
            {
                Log.e("YellLogin", e.toString());
            }
            call.enqueue(new Callback<TokenPair>() {
                @Override
                public void onResponse(Call<TokenPair> call, Response<TokenPair> response) {

                    Log.w("YellLogin", "onResponse: " + response);

                    if (response.isSuccessful()) {
                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        if (response.code() == 401) {
                            ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                            Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        showForm();
                    }

                }

                @Override
                public void onFailure(Call<TokenPair> call, Throwable t) {
                    Log.w("YellLogin", "onFailure: " + t.getMessage());
                    showForm();
                }
            });

        }

    }

    void goToRegister(){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void handleErrors(ResponseBody response) {

        ErrorMessage apiError = ErrorMessage.convertErrors(response);

    }

    public void setupInputRules() {

        //validator.addValidation(this, R.id.usernameInput, Patterns.EMAIL_ADDRESS, R.string.invalid_username);
        validator.addValidation(this, R.id.passwordInput, RegexTemplate.NOT_EMPTY, R.string.invalid_password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}