package com.proconsi.electrobazar.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.repositories.ProductsAdminRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriesViewModel extends ViewModel {

    private final ProductsAdminRepository repository;
    private final MutableLiveData<List<Category>> allCategories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Category>> filteredCategories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private String currentSearchQuery = "";

    public CategoriesViewModel() {
        this.repository = new ProductsAdminRepository();
    }

    public LiveData<List<Category>> getFilteredCategories() { return filteredCategories; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSuccessMessage() { return successMessage; }

    public void loadCategories() {
        isLoading.setValue(true);
        repository.getCategories(new ProductsAdminRepository.DataCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                allCategories.setValue(categories);
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

    public void filterBySearch(String query) {
        currentSearchQuery = query.toLowerCase();
        applyFilters();
    }

    private void applyFilters() {
        List<Category> categories = allCategories.getValue();
        if (categories == null) return;

        List<Category> filtered = categories.stream()
                .filter(c -> c.getName().toLowerCase().contains(currentSearchQuery) || 
                            (c.getDescription() != null && c.getDescription().toLowerCase().contains(currentSearchQuery)))
                .collect(Collectors.toList());

        filteredCategories.setValue(filtered);
    }

    public void createCategory(Category category) {
        isLoading.setValue(true);
        repository.createCategory(category, new ProductsAdminRepository.DataCallback<Category>() {
            @Override
            public void onSuccess(Category category) {
                successMessage.setValue("Categoría creada");
                loadCategories();
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void updateCategory(Long id, Category category) {
        isLoading.setValue(true);
        repository.updateCategory(id, category, new ProductsAdminRepository.DataCallback<Category>() {
            @Override
            public void onSuccess(Category category) {
                successMessage.setValue("Categoría actualizada");
                loadCategories();
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void deleteCategory(Long id) {
        isLoading.setValue(true);
        repository.deleteCategory(id, new ProductsAdminRepository.DataCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                successMessage.setValue("Categoría eliminada");
                loadCategories();
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
