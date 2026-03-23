package com.proconsi.electrobazar.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailRepository {
    private final ApiService apiService;

    public EmailRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public LiveData<String> sendSaleEmail(Long saleId, String email) {
        MutableLiveData<String> result = new MutableLiveData<>();
        apiService.sendSaleEmail(saleId, email).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    result.postValue("SUCCESS");
                } else {
                    result.postValue("Error al enviar email: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                result.postValue("Error de red: " + t.getMessage());
            }
        });
        return result;
    }
}
