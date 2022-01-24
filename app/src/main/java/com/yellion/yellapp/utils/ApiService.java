package com.yellion.yellapp.utils;

import com.yellion.yellapp.models.BudgetCard;
import com.yellion.yellapp.models.InfoMessage;
import com.yellion.yellapp.models.TokenPair;
import com.yellion.yellapp.models.TransactionCard;
import com.yellion.yellapp.models.UserAccount;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("users")
    @Headers("Content-Type: application/json")
    Call<InfoMessage> register(@Body RequestBody body);

    @POST("auth")
    @Headers("Content-Type: application/json")
    Call<TokenPair> login(@Body RequestBody body);

    @GET("auth")
    Call<TokenPair> refresh(@Header("Authorization") String refreshToken);

    @GET("users")
    Call<UserAccount> getUserProfile(@Query("fetch") String fetch);

    @DELETE("auth")
    Call<InfoMessage> logout();

    @POST("users/verify")
    @Headers("Content-Type: application/json")
    Call<InfoMessage> verify(@Body RequestBody body);

    @POST("users/verify/resend")
    @Headers("Content-Type: application/json")
    Call<InfoMessage> resendVerification(@Body RequestBody body);

    @POST("budgets")
    @Headers("Content-Type: application/json")
    Call<BudgetCard> createBudget(@Body RequestBody body);

    @GET("budgets")
    Call<BudgetCard> getBudget(@Query("budget_id") String budgetID, @Query("fetch") String fetch);


    @PATCH("budgets")
    @Headers("Content-Type: application/json")
    Call<InfoMessage> editBudgets(@Body RequestBody body);

    @HTTP(method = "DELETE", path = "budgets", hasBody = true)
    @Headers("Content-Type: application/json")
    Call<InfoMessage> deleteBudgets(@Body RequestBody body);


    @GET("transactions")
    Call<TransactionCard> getTransaction(@Query("transaction_id") String transactionID);

    @POST("transactions")
    Call<TransactionCard> addTransaction(@Body RequestBody body);

    @PATCH("transactions")
    Call<InfoMessage> editTransaction(@Body RequestBody body);

    @DELETE("transactions")
    @Headers("Content-Type: application/json")
    Call<InfoMessage> deleteTransaction(@Body RequestBody body);

}