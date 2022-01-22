package com.yellion.yellapp.utils;

import com.yellion.yellapp.models.DashboardCard;
import com.yellion.yellapp.models.InfoMessage;
import com.yellion.yellapp.models.YellTask;
import com.yellion.yellapp.models.TokenPair;
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

    @POST("dashboards")
    @Headers("Content-Type: application/json")
    Call<DashboardCard> addDashboard(@Body RequestBody body);

    @GET("dashboards")
    Call<DashboardCard> getDashboard(@Query("dashboard_id") String dashboardId, @Query("fetch") String fetch);

    @PATCH("dashboards")
    @Headers("Content-Type: application/json")
    Call<InfoMessage> editDashboard(@Body RequestBody body);

    @HTTP(method = "DELETE", path = "dashboards", hasBody = true)
    @Headers("Content-Type: application/json")
    Call<InfoMessage> deleteDashboard(@Body RequestBody body);

    @GET("tasks")
    Call<YellTask> getTask(@Query("task_id") String taskId);

    @POST("users")
    @Headers("Content-Type: application/json")
    Call<InfoMessage> addTask(@Body RequestBody body);



    @PATCH("users")
    @Headers("Content-Type: application/json")
    Call<InfoMessage> editTask(@Body RequestBody body);

    @DELETE("users")
    @Headers("Content-Type: application/json")
    Call<InfoMessage> deleteTask(@Body RequestBody body);
}