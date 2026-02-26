package com.example.electrobazar.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String DEFAULT_BASE_URL = "https://api.danis.studio/";
    private static String baseUrl = DEFAULT_BASE_URL;
    private static Retrofit retrofit;

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String url) {
        if (!url.equals(baseUrl)) {
            baseUrl = url;
            retrofit = null; // Force rebuild
        }
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}