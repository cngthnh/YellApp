package com.yellion.yellapp.models;

import com.squareup.moshi.Json;

public class BudgetId {
    @Json(name = "message")
    public String message;
    @Json(name = "budget_id")
    public String budgetId;

    public String getMessage() {
        return message;
    }
    public String getBudgetId() {
        return budgetId;
    }
    public BudgetId(String message, String budgetid)
    {
        this.message = message;
        this.budgetId=budgetid;

    }

}
