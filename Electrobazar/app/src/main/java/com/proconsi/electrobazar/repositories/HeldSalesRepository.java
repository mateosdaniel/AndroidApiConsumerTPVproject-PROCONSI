package com.proconsi.electrobazar.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proconsi.electrobazar.models.SuspendRequest;
import com.proconsi.electrobazar.models.SuspendedSaleResponse;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeldSalesRepository {
    private final ApiService apiService;

    public HeldSalesRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public LiveData<List<SuspendedSaleResponse>> getHeldSales() {
        MutableLiveData<List<SuspendedSaleResponse>> data = new MutableLiveData<>();
        apiService.getSuspendedSales().enqueue(new Callback<List<SuspendedSaleResponse>>() {
            @Override
            public void onResponse(Call<List<SuspendedSaleResponse>> call, Response<List<SuspendedSaleResponse>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<SuspendedSaleResponse>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<SuspendedSaleResponse> holdSale(SuspendRequest request) {
        MutableLiveData<SuspendedSaleResponse> data = new MutableLiveData<>();
        apiService.suspendSale(request).enqueue(new Callback<SuspendedSaleResponse>() {
            @Override
            public void onResponse(Call<SuspendedSaleResponse> call, Response<SuspendedSaleResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<SuspendedSaleResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<SuspendedSaleResponse> recoverSale(Long id) {
        MutableLiveData<SuspendedSaleResponse> data = new MutableLiveData<>();
        apiService.resumeSuspendedSale(id).enqueue(new Callback<SuspendedSaleResponse>() {
            @Override
            public void onResponse(Call<SuspendedSaleResponse> call, Response<SuspendedSaleResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<SuspendedSaleResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Boolean> deleteHeldSale(Long id) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        apiService.cancelSuspendedSale(id).enqueue(new Callback<SuspendedSaleResponse>() {
            @Override
            public void onResponse(Call<SuspendedSaleResponse> call, Response<SuspendedSaleResponse> response) {
                data.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<SuspendedSaleResponse> call, Throwable t) {
                data.setValue(false);
            }
        });
        return data;
    }
}
