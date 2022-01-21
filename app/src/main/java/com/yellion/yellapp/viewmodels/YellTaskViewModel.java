package com.yellion.yellapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yellion.yellapp.models.YellTask;
import com.yellion.yellapp.repository.YellTaskRepository;

public class YellTaskViewModel extends AndroidViewModel {
    private YellTaskRepository yellTaskRepository;
    private LiveData<YellTask> yellTaskLiveData;

    public YellTaskViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        yellTaskRepository = new YellTaskRepository(getApplication());
        yellTaskLiveData = yellTaskRepository.getYellTaskResponseLiveData();
    }

    public void getTask(String taskId) {
        yellTaskRepository.getTaskFromServer(taskId);
    }

    public LiveData<YellTask> getYellTaskLiveData() {
        return yellTaskLiveData;
    }
}