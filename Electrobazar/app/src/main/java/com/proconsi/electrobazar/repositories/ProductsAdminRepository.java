package com.proconsi.electrobazar.repositories;

import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.ProductRequest;
import com.proconsi.electrobazar.models.TaxRate;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsAdminRepository {

    private final ApiService apiService;

    public ProductsAdminRepository() {
        this.apiService = RetrofitClient.getInstance().getApi();
    }

    public void getProducts(final DataCallback<List<Product>> callback) {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getCategories(final DataCallback<List<Category>> callback) {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getTaxRates(final DataCallback<List<TaxRate>> callback) {
        apiService.getTaxRates().enqueue(new Callback<List<TaxRate>>() {
            @Override
            public void onResponse(Call<List<TaxRate>> call, Response<List<TaxRate>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<TaxRate>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createProduct(ProductRequest request, final DataCallback<Product> callback) {
        apiService.createProduct(request).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateProduct(Long id, ProductRequest request, final DataCallback<Product> callback) {
        apiService.updateProduct(id, request).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteProduct(Long id, final DataCallback<Void> callback) {
        apiService.deleteProduct(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createCategory(Category category, final DataCallback<Category> callback) {
        apiService.createCategory(category).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateCategory(Long id, Category category, final DataCallback<Category> callback) {
        apiService.updateCategory(id, category).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteCategory(Long id, final DataCallback<Void> callback) {
        apiService.deleteCategory(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void hardDeleteProduct(Long id, final DataCallback<Void> callback) {
        apiService.hardDeleteProduct(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void uploadCsv(MultipartBody.Part file, final DataCallback<Map<String, Object>> callback) {
        apiService.uploadCsv(file).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
}
