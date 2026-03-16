package com.proconsi.electrobazar.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proconsi.electrobazar.models.Tariff;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TariffsAdminRepository {
    private final ApiService apiService;

    public TariffsAdminRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public LiveData<List<Tariff>> getTariffs(boolean includeInactive) {
        MutableLiveData<List<Tariff>> data = new MutableLiveData<>();
        apiService.getTariffs(includeInactive).enqueue(new Callback<List<Tariff>>() {
            @Override
            public void onResponse(Call<List<Tariff>> call, Response<List<Tariff>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Tariff>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Map<Long, Long>> getCustomerCounts() {
        MutableLiveData<Map<Long, Long>> data = new MutableLiveData<>();
        apiService.getTariffCustomerCounts().enqueue(new Callback<Map<Long, Long>>() {
            @Override
            public void onResponse(Call<Map<Long, Long>> call, Response<Map<Long, Long>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(new HashMap<>());
                }
            }

            @Override
            public void onFailure(Call<Map<Long, Long>> call, Throwable t) {
                data.setValue(new HashMap<>());
            }
        });
        return data;
    }

    public LiveData<Tariff> createTariff(String name, double discount, String description) {
        MutableLiveData<Tariff> data = new MutableLiveData<>();
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("discountPercentage", discount);
        body.put("description", description);

        apiService.createTariff(body).enqueue(new Callback<Tariff>() {
            @Override
            public void onResponse(Call<Tariff> call, Response<Tariff> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Tariff> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Tariff> updateTariff(Long id, double discount, String description) {
        MutableLiveData<Tariff> data = new MutableLiveData<>();
        Map<String, Object> body = new HashMap<>();
        body.put("discountPercentage", discount);
        body.put("description", description);

        apiService.updateTariff(id, body).enqueue(new Callback<Tariff>() {
            @Override
            public void onResponse(Call<Tariff> call, Response<Tariff> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Tariff> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Boolean> activateTariff(Long id) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        apiService.activateTariff(id).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                data.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                data.setValue(false);
            }
        });
        return data;
    }

    public LiveData<Boolean> deactivateTariff(Long id) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        apiService.deactivateTariff(id).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                data.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                data.setValue(false);
            }
        });
        return data;
    }
}
