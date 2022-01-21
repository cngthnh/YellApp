package com.yellion.yellapp.repository;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.squareup.moshi.Moshi;
import com.yellion.yellapp.R;
import com.yellion.yellapp.models.ErrorMessage;
import com.yellion.yellapp.models.InfoMessage;
import com.yellion.yellapp.models.YellTask;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YellTaskRepository {
    SessionManager sessionManager;
    ApiService service;
    SharedPreferences sharedPreferences;
    Application application;
    Moshi moshi = new Moshi.Builder().build();
    private MutableLiveData<YellTask> YellTaskResponseLiveData;

    public YellTaskRepository(Application application) {
        this.application = application;
        sharedPreferences = application.getSharedPreferences(application.getResources().getString(R.string.yell_sp), MODE_PRIVATE);
        sessionManager = SessionManager.getInstance(sharedPreferences);
        service = Client.createService(ApiService.class);
        YellTaskResponseLiveData = new MutableLiveData<>();
    }

    public void getTaskFromServer(String taskId) {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<YellTask> call;
        call = service.getTask(taskId);
        call.enqueue(new Callback<YellTask>() {
            @Override
            public void onResponse(Call<YellTask> call, Response<YellTask> response) {
                if (response.isSuccessful()) {
                    YellTaskResponseLiveData.postValue(response.body());
                } else {
                    ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                    Toast.makeText(application.getApplicationContext(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YellTask> call, Throwable t) {
                YellTaskResponseLiveData.postValue(null);
                Log.w("YellTaskFragment", "onFailure: " + t.getMessage() );
            }
        });
    }

    public MutableLiveData<YellTask> getYellTaskResponseLiveData() {
        return YellTaskResponseLiveData;
    }

    private void addTaskToServer(YellTask yellTask) {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<InfoMessage> call;
        RequestBody requestBody = taskToJson(yellTask);
        call = service.addTask(requestBody);
        call.enqueue(new Callback<InfoMessage>() {
            @Override
            public void onResponse(Call<InfoMessage> call, Response<InfoMessage> response) {

                Log.w("YellTaskCreate", "onResponse: " + response);

                if (response.isSuccessful()) {
                    // TODO
                }
                else {
                    if (response.code() == 401) {
                        ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                        Toast.makeText(application.getApplicationContext(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    // TODO
                }

            }

            @Override
            public void onFailure(Call<InfoMessage> call, Throwable t) {
                Toast.makeText(application.getApplicationContext(), "Lỗi khi kết nối với server", Toast.LENGTH_LONG).show();
                // TODO:
            }
        });
    }
    private RequestBody taskToJson(YellTask currentYellTask) {
        String jsonYellTask = moshi.adapter(YellTask.class).toJson(currentYellTask);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), jsonYellTask);
        return requestBody;
    }
}
