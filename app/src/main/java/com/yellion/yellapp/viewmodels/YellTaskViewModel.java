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
    private  LiveData<String> taskId;
    public YellTaskViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        yellTaskRepository = new YellTaskRepository(getApplication());
        yellTaskLiveData = yellTaskRepository.getYellTaskResponseLiveData();
        taskId = yellTaskRepository.getTaskIdLiveData();
    }

    public void getTask(String taskId) {
        yellTaskRepository.getTaskFromServer(taskId);
    }

    public void addTask(YellTask yellTask) {
       yellTaskRepository.addTaskToServer(yellTask);
    }

    public void editTask(YellTask yellTask) {
        yellTaskRepository.patchTaskToServer(yellTask);
    }

    public LiveData<YellTask> getYellTaskLiveData() {
        return yellTaskLiveData;
    }

    public  LiveData<String> getTaskIdLiveData() {return taskId;}
}
