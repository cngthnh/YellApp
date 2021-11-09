package com.yellion.yellapp.utils;

import android.content.SharedPreferences;

import com.yellion.yellapp.models.TokenPair;

public class TokenManager {
    private SharedPreferences prefs;

    private static TokenManager INSTANCE = null;

    private TokenManager(SharedPreferences prefs){
        this.prefs = prefs;
    }

    public static synchronized TokenManager getInstance(SharedPreferences prefs){
        if(INSTANCE == null){
            INSTANCE = new TokenManager(prefs);
        }
        return INSTANCE;
    }

    public void saveToken(TokenPair token){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ACCESS_TOKEN", token.getAccessToken());
        editor.putString("REFRESH_TOKEN", token.getRefreshToken());
        editor.apply();
    }

    public void deleteToken(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("ACCESS_TOKEN");
        editor.remove("REFRESH_TOKEN");
        editor.apply();
    }

    public TokenPair getToken(){
        TokenPair token = new TokenPair();
        token.setAccessToken(prefs.getString("ACCESS_TOKEN", null));
        token.setRefreshToken(prefs.getString("REFRESH_TOKEN", null));
        return token;
    }
}
