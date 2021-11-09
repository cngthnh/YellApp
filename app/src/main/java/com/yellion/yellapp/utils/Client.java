package com.yellion.yellapp.utils;

import com.yellion.yellapp.models.TokenPair;

import java.io.IOException;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class Client {
    private final static String API_URL = "https://yell-backend.herokuapp.com/api/";
    private final static OkHttpClient client = buildClient();
    private final static Retrofit retrofit = buildRetrofit(client);

    private static OkHttpClient buildClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        Request.Builder builder = request.newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Connection", "close");

                        request = builder.build();

                        return chain.proceed(request);

                    }
                });

        return builder.build();

    }

    private static Retrofit buildRetrofit(OkHttpClient client){
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static <T> T createService(Class<T> service){
        return retrofit.create(service);
    }

    public static <T> T createServiceWithAuth(Class<T> service, final TokenManager tokenManager){

        class accessTokenBinder implements  Interceptor {

            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder builder = request.newBuilder();

                if (tokenManager.getToken().getAccessToken() != null) {
                    builder.addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());
                }
                request = builder.build();
                return chain.proceed(request);
            }
        }

        class tokenMaintainer implements Interceptor {

            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Response response = chain.proceed(request);

                if (response.code() == 401) {

                    if (responseCount(response) >= 3) {
                        tokenManager.deleteToken();
                        response.close();
                        return response;
                    }

                    response.close();

                    TokenPair token = tokenManager.getToken();

                    ApiService service = Client.createService(ApiService.class);
                    Call<TokenPair> call = service.refresh("Bearer " + token.getRefreshToken());
                    retrofit2.Response<TokenPair> res = call.execute();

                    int resCode = res.code();

                    if (resCode == 200) {
                        TokenPair newToken = res.body();
                        tokenManager.saveToken(newToken);
                        request = chain.request().newBuilder().header("Authorization", "Bearer " + newToken.getAccessToken()).build();
                        return chain.proceed(request);
                    }
                    // invalid session
                    else if (resCode == 403) {
                        tokenManager.deleteToken();
                        return response;
                    }
                }

                return response;
            }
        }

        OkHttpClient newClient = client.newBuilder()
                .addInterceptor(new accessTokenBinder())
                .addInterceptor(new tokenMaintainer())
                .build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }

    public static Retrofit getInstance() {
        return retrofit;
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
