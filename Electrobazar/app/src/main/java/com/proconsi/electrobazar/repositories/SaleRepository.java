package com.proconsi.electrobazar.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proconsi.electrobazar.models.SaleWithTaxRequest;
import com.proconsi.electrobazar.models.SaleWithTaxResponse;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleRepository {
    private final ApiService apiService;

    public SaleRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public LiveData<SaleWithTaxResponse> createSale(SaleWithTaxRequest request) {
        MutableLiveData<SaleWithTaxResponse> data = new MutableLiveData<>();
        apiService.createSaleWithTax(request).enqueue(new Callback<SaleWithTaxResponse>() {
            @Override
            public void onResponse(Call<SaleWithTaxResponse> call, Response<SaleWithTaxResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<SaleWithTaxResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
