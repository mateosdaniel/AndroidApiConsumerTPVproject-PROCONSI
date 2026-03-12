package com.proconsi.electrobazar.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductViewModel extends ViewModel {
    private final ProductRepository repository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Long> selectedCategoryId = new MutableLiveData<>(null);
    
    private LiveData<List<Product>> products;
    private LiveData<List<Category>> categories;

    public ProductViewModel() {
        this.repository = new ProductRepository();
        this.categories = repository.getCategories();
        this.products = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.isEmpty()) {
                return Transformations.map(repository.getAllProducts(), allProducts -> {
                    if (allProducts == null) return new ArrayList<>();
                    Long catId = selectedCategoryId.getValue();
                    if (catId == null) return allProducts;
                    return allProducts.stream()
                            .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(catId))
                            .collect(Collectors.toList());
                });
            } else {
                return repository.searchProducts(query);
            }
        });
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void loadProducts() {
        searchQuery.setValue("");
        selectedCategoryId.setValue(null);
    }

    public void searchProducts(String query) {
        searchQuery.setValue(query);
    }

    public void filterByCategory(Category category) {
        if (category == null) {
            selectedCategoryId.setValue(null);
        } else {
            selectedCategoryId.setValue(category.getId());
        }
        // Trigger refresh
        searchQuery.setValue(searchQuery.getValue());
    }
}
