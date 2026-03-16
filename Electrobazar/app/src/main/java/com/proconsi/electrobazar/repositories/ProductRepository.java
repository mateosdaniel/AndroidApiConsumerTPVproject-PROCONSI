package com.proconsi.electrobazar.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private final ApiService apiService;

    public ProductRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public LiveData<List<Product>> getAllProducts() {
        MutableLiveData<List<Product>> data = new MutableLiveData<>();
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Product>> searchProducts(String query) {
        MutableLiveData<List<Product>> data = new MutableLiveData<>();
        apiService.searchProducts(query).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<Category>> getCategories() {
        MutableLiveData<List<Category>> data = new MutableLiveData<>();
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<com.proconsi.electrobazar.models.PriceResponse> getProductPriceByTariff(Long productId, Long tariffId) {
        MutableLiveData<com.proconsi.electrobazar.models.PriceResponse> data = new MutableLiveData<>();
        apiService.getProductPriceByTariff(productId, tariffId).enqueue(new Callback<com.proconsi.electrobazar.models.PriceResponse>() {
            @Override
            public void onResponse(Call<com.proconsi.electrobazar.models.PriceResponse> call, Response<com.proconsi.electrobazar.models.PriceResponse> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<com.proconsi.electrobazar.models.PriceResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
