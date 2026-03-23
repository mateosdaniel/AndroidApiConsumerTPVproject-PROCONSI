package com.proconsi.electrobazar.repositories;

import com.proconsi.electrobazar.models.ActivityLog;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.Sale;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardRepository {

    private final ApiService apiService;

    public DashboardRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public void getSalesRange(String from, String to, final DataCallback<List<Sale>> callback) {
        apiService.getSalesRange(from, to).enqueue(new retrofit2.Callback<com.proconsi.electrobazar.models.PagedResponse<Sale>>() {
            @Override
            public void onResponse(retrofit2.Call<com.proconsi.electrobazar.models.PagedResponse<Sale>> call, retrofit2.Response<com.proconsi.electrobazar.models.PagedResponse<Sale>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getContent());
                } else {
                    callback.onError("Error fetching sales: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.proconsi.electrobazar.models.PagedResponse<Sale>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getProducts(final DataCallback<List<Product>> callback) {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error fetching products: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getRecentActivity(final DataCallback<List<ActivityLog>> callback) {
        apiService.getRecentActivityLogs().enqueue(new Callback<List<ActivityLog>>() {
            @Override
            public void onResponse(Call<List<ActivityLog>> call, Response<List<ActivityLog>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error fetching activity logs: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ActivityLog>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getDashboardStats(String period, final DataCallback<com.proconsi.electrobazar.models.DashboardStats> callback) {
        apiService.getDashboardStats(period).enqueue(new Callback<com.proconsi.electrobazar.models.DashboardStats>() {
            @Override
            public void onResponse(Call<com.proconsi.electrobazar.models.DashboardStats> call, Response<com.proconsi.electrobazar.models.DashboardStats> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error fetching stats: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.proconsi.electrobazar.models.DashboardStats> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
}
