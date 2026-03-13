package com.proconsi.electrobazar.repositories;

import com.proconsi.electrobazar.models.Sale;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoicesAdminRepository {
    private final ApiService apiService;

    public InvoicesAdminRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public void getSales(final RepositoryCallback<List<Sale>> callback) {
        apiService.getSales().enqueue(new Callback<List<Sale>>() {
            @Override
            public void onResponse(Call<List<Sale>> call, Response<List<Sale>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error fetching sales: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Sale>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getSalesRange(String from, String to, final RepositoryCallback<List<Sale>> callback) {
        apiService.getSalesRange(from, to).enqueue(new Callback<List<Sale>>() {
            @Override
            public void onResponse(Call<List<Sale>> call, Response<List<Sale>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error fetching sales range: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Sale>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void cancelSale(Long id, Long workerId, String reason, final RepositoryCallback<Void> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("reason", reason);
        apiService.cancelSale(id, body, workerId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error cancelling sale: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void downloadInvoice(Long id, final RepositoryCallback<ResponseBody> callback) {
        apiService.downloadInvoice(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error downloading invoice: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
