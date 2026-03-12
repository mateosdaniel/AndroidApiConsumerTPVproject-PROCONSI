package com.proconsi.electrobazar.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proconsi.electrobazar.models.LoginRequest;
import com.proconsi.electrobazar.models.LoginResponse;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final ApiService apiService;

    public AuthRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public LiveData<LoginResult> login(String username, String password) {
        MutableLiveData<LoginResult> result = new MutableLiveData<>();
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(new LoginResult(response.body()));
                } else {
                    String error = "Error en el login";
                    if (response.code() == 401) {
                        error = "Usuario o contraseña incorrectos";
                    }
                    result.setValue(new LoginResult(error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                result.setValue(new LoginResult("Error de conexión: " + t.getMessage()));
            }
        });

        return result;
    }

    public static class LoginResult {
        private LoginResponse success;
        private String error;

        public LoginResult(LoginResponse success) {
            this.success = success;
        }

        public LoginResult(String error) {
            this.error = error;
        }

        public LoginResponse getSuccess() { return success; }
        public String getError() { return error; }
    }
}
