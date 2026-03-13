package com.proconsi.electrobazar.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.ProductRequest;
import com.proconsi.electrobazar.models.TaxRate;
import com.proconsi.electrobazar.repositories.ProductsAdminRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsViewModel extends ViewModel {

    private final ProductsAdminRepository repository;
    private final MutableLiveData<List<Product>> allProducts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Product>> filteredProducts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<TaxRate>> taxRates = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private String currentSearchQuery = "";
    private Long currentCategoryId = null;

    public ProductsViewModel() {
        this.repository = new ProductsAdminRepository();
    }

    public LiveData<List<Product>> getFilteredProducts() { return filteredProducts; }
    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<TaxRate>> getTaxRates() { return taxRates; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSuccessMessage() { return successMessage; }

    public void loadProducts() {
        isLoading.setValue(true);
        repository.getProducts(new ProductsAdminRepository.DataCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                allProducts.setValue(products);
                applyFilters();
                isLoading.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void loadCategories() {
        repository.getCategories(new ProductsAdminRepository.DataCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> cats) {
                categories.setValue(cats);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public void loadTaxRates() {
        repository.getTaxRates(new ProductsAdminRepository.DataCallback<List<TaxRate>>() {
            @Override
            public void onSuccess(List<TaxRate> rates) {
                taxRates.setValue(rates);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public void filterBySearch(String query) {
        currentSearchQuery = query.toLowerCase();
        applyFilters();
    }

    public void filterByCategory(Long categoryId) {
        currentCategoryId = categoryId;
        applyFilters();
    }

    private void applyFilters() {
        List<Product> products = allProducts.getValue();
        if (products == null) return;

        List<Product> filtered = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(currentSearchQuery))
                .filter(p -> currentCategoryId == null || (p.getCategory() != null && p.getCategory().getId().equals(currentCategoryId)))
                .collect(Collectors.toList());

        filteredProducts.setValue(filtered);
    }

    public void createProduct(ProductRequest request) {
        isLoading.setValue(true);
        repository.createProduct(request, new ProductsAdminRepository.DataCallback<Product>() {
            @Override
            public void onSuccess(Product product) {
                successMessage.setValue("Producto creado correctamente");
                loadProducts();
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void updateProduct(Long id, ProductRequest request) {
        isLoading.setValue(true);
        repository.updateProduct(id, request, new ProductsAdminRepository.DataCallback<Product>() {
            @Override
            public void onSuccess(Product product) {
                successMessage.setValue("Producto actualizado correctamente");
                loadProducts();
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void deleteProduct(Long id) {
        isLoading.setValue(true);
        repository.deleteProduct(id, new ProductsAdminRepository.DataCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                successMessage.setValue("Producto desactivado");
                loadProducts();
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void hardDeleteProduct(Long id) {
        isLoading.setValue(true);
        repository.hardDeleteProduct(id, new ProductsAdminRepository.DataCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                successMessage.setValue("Producto eliminado definitivamente");
                loadProducts();
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void clearMessages() {
        errorMessage.setValue(null);
        successMessage.setValue(null);
    }
}
