package com.yellion.yellapp.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yellion.yellapp.models.DashboardCard;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {
    private MutableLiveData<List<DashboardCard>> mListDashboardLiveData;
    private List<DashboardCard> mListDashboard;

    public DashboardViewModel(List<DashboardCard> mListDashboard) {
        mListDashboardLiveData = new MutableLiveData<>();
        this.mListDashboard = mListDashboard;
        initData();
    }

    private void initData() {
        mListDashboard.add(new DashboardCard("Đồ án"));
        mListDashboardLiveData.setValue(mListDashboard);
    }

    public MutableLiveData<List<DashboardCard>> getListDashboardLiveData() {
        return mListDashboardLiveData;
    }

    public void addDashboard(DashboardCard dashboardCard){
        mListDashboard.add(dashboardCard);
        mListDashboardLiveData.setValue(mListDashboard);
    }
}
