package com.proconsi.electrobazar.repositories;

import com.proconsi.electrobazar.models.Customer;
import com.proconsi.electrobazar.models.CustomerRequest;
import com.proconsi.electrobazar.models.Tariff;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrmAdminRepository {

    private final ApiService apiService;

    public CrmAdminRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public void getCustomers(OnResultListener<List<Customer>> listener) {
        apiService.getCustomers().enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void searchCustomers(String query, OnResultListener<List<Customer>> listener) {
        apiService.searchCustomers(query).enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void createCustomer(CustomerRequest request, OnResultListener<Customer> listener) {
        apiService.createCustomer(request).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void updateCustomer(Long id, CustomerRequest request, OnResultListener<Customer> listener) {
        apiService.updateCustomer(id, request).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void deleteCustomer(Long id, OnResultListener<Void> listener) {
        apiService.deleteCustomer(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(null);
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void getTariffs(OnResultListener<List<Tariff>> listener) {
        // According to user: GET /api/tariffs
        // Checking ApiService for this: @GET("api/tariffs") - Call<List<Tariff>> getTariffs(@Query("includeInactive") boolean includeInactive);
        apiService.getTariffs(false).enqueue(new Callback<List<Tariff>>() {
            @Override
            public void onResponse(Call<List<Tariff>> call, Response<List<Tariff>> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Tariff>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
