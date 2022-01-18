package com.yellion.yellapp.models;

import com.squareup.moshi.Json;

public class DashboardCard {

    @Json(name = "name")
    public String name;

    public DashboardCard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
