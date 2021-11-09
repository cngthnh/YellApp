package com.yellion.yellapp.models;

import com.squareup.moshi.Json;

public class UserCredentials {
    @Json(name = "uid")
    public String uid;
    @Json(name = "hash")
    public String hash;


    public UserCredentials(String uid, String hash)
    {
        this.uid = uid;
        this.hash = hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
