package com.proconsi.electrobazar.repositories;

import com.proconsi.electrobazar.models.Role;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RolesAdminRepository {

    private final ApiService apiService;

    public RolesAdminRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
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

    public void createRole(Role role, OnResultListener<Role> listener) {
        apiService.createRole(role).enqueue(new Callback<Role>() {
            @Override
            public void onResponse(Call<Role> call, Response<Role> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Role> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void updateRole(Long id, Role role, OnResultListener<Role> listener) {
        apiService.updateRole(id, role).enqueue(new Callback<Role>() {
            @Override
            public void onResponse(Call<Role> call, Response<Role> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Role> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void deleteRole(Long id, OnResultListener<Void> listener) {
        apiService.deleteRole(id).enqueue(new Callback<Void>() {
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

    public void getPermissions(OnResultListener<List<String>> listener) {
        apiService.getPermissions().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
