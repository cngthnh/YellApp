package com.yellion.yellapp.models;

import com.squareup.moshi.Json;

public class TransactionCard {
    @Json(name = "budget_id")
    public String budget_id;
    @Json(name = "transaction_id")
    public String transaction_id;
    @Json(name = "content")
    public String content;
    @Json(name = "amount")
    public long amount;
    @Json(name = "type")
    public Integer type;
    @Json(name = "purposes")
    public String purposes;
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
        return transaction_id;
    }
    public void setTran_id(String id) {
        this.transaction_id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getAmount() {
        return amount;
    }
    public void setAmount(long amount) {
        this.amount = amount;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public String getPurpose() {
        return this.purposes;
    }
    public void setPurpose(String purpose) {
        this.purposes = purpose;
    }

    public TransactionCard(String content,int amount, Integer type,String purpose){
     this.content =content;
     this.amount =amount;
     this.type =type;
     this.purposes =purpose;
    }
    public TransactionCard(String budget_id,String tran_id,String content,int amount,int type,String purpose,String created_at){
        this.budget_id=budget_id;
        this.transaction_id=tran_id;
        this.content=content;
        this.amount=amount;
        this.type=type;
        this.purposes=purpose;
        this.created_at=created_at;
    }
}