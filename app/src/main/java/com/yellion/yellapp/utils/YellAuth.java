package com.yellion.yellapp.utils;

import androidx.annotation.Nullable;

import com.yellion.yellapp.models.TokenPair;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class YellAuth implements Authenticator {
    private TokenManager tokenManager;
    private static YellAuth INSTANCE;

    private YellAuth(TokenManager tokenManager){
        this.tokenManager = tokenManager;
    }

    static synchronized YellAuth getInstance(TokenManager tokenManager){
        if(INSTANCE == null){
            INSTANCE = new YellAuth(tokenManager);
        }

        return INSTANCE;
    }


    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        if (responseCount(response) >= 3) {
            tokenManager.deleteToken();
            return null;
        }

        TokenPair token = tokenManager.getToken();

        ApiService service = Client.createService(ApiService.class);
        Call<TokenPair> call = service.refresh(token.getRefreshToken());
        retrofit2.Response<TokenPair> res = call.execute();

        if (res.isSuccessful()) {
            TokenPair newToken = res.body();
            tokenManager.saveToken(newToken);

            return response.request().newBuilder().header("Authorization", "Bearer " + res.body().getAccessToken()).build();
        }
        else {
            return null;
        }
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
