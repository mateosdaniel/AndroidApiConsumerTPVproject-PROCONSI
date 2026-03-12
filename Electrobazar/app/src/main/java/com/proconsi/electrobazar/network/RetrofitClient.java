package com.proconsi.electrobazar.network;

import android.content.Context;
import com.proconsi.electrobazar.utils.SessionManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://api.danis.studio/";
    private static RetrofitClient instance = null;
    private final ApiService apiService;

    private RetrofitClient(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(sessionManager))
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        
        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            throw new RuntimeException("RetrofitClient must be initialized in Application.onCreate()");
        }
        return instance;
    }

    public ApiService getApi() {
        return apiService;
    }
}
