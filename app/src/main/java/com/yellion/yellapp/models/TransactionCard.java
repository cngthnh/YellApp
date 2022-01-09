package com.yellion.yellapp.models;

import com.squareup.moshi.Json;

public class TransactionCard {
    @Json(name = "budget_id")
    public String budget_id;
    @Json(name = "time")
    public String time;
    @Json(name = "amount")
    public Integer amount;
    @Json(name = "purpose")
    public String purpose;

    public TransactionCard() {
    }

    public String getBudget_id() {
        return budget_id;
    }
    public void setBudget_id(String budget_id) {
        this.budget_id = budget_id;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String name) {
        this.time = time;
    }
    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    public String getPurpose() {
        return purpose;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    public TransactionCard(String name,String time, Integer amount,String purpose){
     this.budget_id =name;
     this.time=time;
     this.amount=amount;
     this.purpose=purpose;
    }
}