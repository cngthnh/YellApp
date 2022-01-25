package com.yellion.yellapp.models;

import com.squareup.moshi.Json;

public class TransactionCard {
    @Json(name = "budget_id")
    public String budget_id;
    @Json(name = "tran_id")
    public String tran_id;
    @Json(name = "content")
    public String content;
    @Json(name = "amount")
    public int amount;
    @Json(name = "type")
    public Integer type;
    @Json(name = "purpose")
    public String purpose;
    @Json(name = "created_at")
    public String created_at;

    public TransactionCard() {
    }
    public String getBudget_id() {
        return budget_id;
    }
    public void setBudget_id(String budget_id) {
        this.budget_id = budget_id;
    }

    public String getCreated_at() {
        return created_at;
    }
    public String getTran_id() {
        return tran_id;
    }
    public void setTran_id(String id) {
        this.tran_id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public String getPurpose() {
        return purpose;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public TransactionCard(String content,int amount, Integer type,String purpose){
     this.content =content;
     this.amount =amount;
     this.type =type;
     this.purpose =purpose;
    }
    public TransactionCard(String budget_id,String tran_id,String content,int amount,int type,String purpose,String created_at){
        this.budget_id=budget_id;
        this.tran_id=tran_id;
        this.content=content;
        this.amount=amount;
        this.type=type;
        this.purpose=purpose;
        this.created_at=created_at;
    }
}