package com.proconsi.electrobazar.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proconsi.electrobazar.models.Customer;
import com.proconsi.electrobazar.models.CustomerRequest;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRepository {
    private final ApiService apiService = RetrofitClient.getInstance().getApi();

    public LiveData<List<Customer>> searchCustomers(String query) {
        MutableLiveData<List<Customer>> data = new MutableLiveData<>();
        apiService.searchCustomers(query).enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Customer> createCustomer(CustomerRequest request) {
        MutableLiveData<Customer> data = new MutableLiveData<>();
        apiService.createCustomer(request).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
