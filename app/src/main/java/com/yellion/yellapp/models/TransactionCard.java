package com.yellion.yellapp.models;

import com.squareup.moshi.Json;

public class TransactionCard {
    @Json(name = "content")
    public String content;
    @Json(name = "money")
    public String money;
    @Json(name = "type")
    public Integer type;
    @Json(name = "category")
    public String category;

    public TransactionCard() {
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getMoney() {
        return money;
    }
    public void setMoney(String name) {
        this.money = money;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public TransactionCard(String content,String money, Integer type,String category){
     this.content =content;
     this.money =money;
     this.type =type;
     this.category =category;
    }
}