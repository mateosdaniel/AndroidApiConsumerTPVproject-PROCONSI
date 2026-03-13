package com.proconsi.electrobazar.repositories;

import com.proconsi.electrobazar.models.Role;
import com.proconsi.electrobazar.models.Worker;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkersAdminRepository {

    private final ApiService apiService;

    public WorkersAdminRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public void getWorkers(OnResultListener<List<Worker>> listener) {
        apiService.getAllWorkers().enqueue(new Callback<List<Worker>>() {
            @Override
            public void onResponse(Call<List<Worker>> call, Response<List<Worker>> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Worker>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void createWorker(Worker worker, OnResultListener<Worker> listener) {
        apiService.createWorker(worker).enqueue(new Callback<Worker>() {
            @Override
            public void onResponse(Call<Worker> call, Response<Worker> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Worker> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void updateWorker(Long id, Worker worker, OnResultListener<Worker> listener) {
        apiService.updateWorker(id, worker).enqueue(new Callback<Worker>() {
            @Override
            public void onResponse(Call<Worker> call, Response<Worker> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Worker> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void deactivateWorker(Long id, OnResultListener<Void> listener) {
        apiService.deactivateWorker(id).enqueue(new Callback<Void>() {
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

    public void getRoles(OnResultListener<List<Role>> listener) {
        apiService.getRoles().enqueue(new Callback<List<Role>>() {
            @Override
            public void onResponse(Call<List<Role>> call, Response<List<Role>> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Role>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
