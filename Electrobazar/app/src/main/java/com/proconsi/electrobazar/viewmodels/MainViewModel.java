package com.proconsi.electrobazar.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proconsi.electrobazar.models.CompanySettings;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<CompanySettings> companySettings = new MutableLiveData<>();
    private final ApiService apiService;

    public MainViewModel() {
        apiService = RetrofitClient.getInstance().getApi();
        refreshCompanySettings();
    }

    public LiveData<CompanySettings> getCompanySettings() {
        return companySettings;
    }

    public void refreshCompanySettings() {
        apiService.getCompanySettings().enqueue(new Callback<CompanySettings>() {
            @Override
            public void onResponse(Call<CompanySettings> call, Response<CompanySettings> response) {
                if (response.isSuccessful() && response.body() != null) {
                    companySettings.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<CompanySettings> call, Throwable t) {
                // Silently fail or handle error if needed
            }
        });
    }
}
