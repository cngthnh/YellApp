package com.yellion.yellapp.models;
import com.squareup.moshi.Json;

public class BudgetCard {
    @Json(name = "name")
    public String name;
    @Json(name = "balance")
    public Integer balance;
    @Json(name = "threshold")
    public Integer threshold;
    @Json(name = "start_time")
    public String start_time;
    @Json(name = "end_time")
    public String end_time;
    @Json(name = "created_at")
    public String created_at;
    public BudgetCard(){}
    public BudgetCard(String name, Integer balance, Integer threshold, String start_time, String end_time, String created_at) {
        this.name = name;
        this.balance =balance;
        this.threshold=threshold;
        this.start_time =start_time;
        this.end_time=end_time;
        this.created_at=created_at;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getBalance() {
        return balance;
    }
    public void setBalance(Integer balance) { this.balance = balance; }

    public Integer getThreshold() {
        return threshold;
    }
    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public String getStart_time() { return start_time;}
    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time_time() {return end_time; }
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getCreated_at() { return created_at;}
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}
