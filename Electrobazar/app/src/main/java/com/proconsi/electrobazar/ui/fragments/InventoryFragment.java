package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.ui.adapters.CategoryAdapter;
import com.proconsi.electrobazar.ui.adapters.InventoryProductAdapter;
import com.proconsi.electrobazar.viewmodels.ProductsViewModel;

public class InventoryFragment extends Fragment {

    private ProductsViewModel viewModel;
    private InventoryProductAdapter adapter;
    private CategoryAdapter categoryAdapter;
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(view, requireContext());
        
        viewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = view.findViewById(R.id.rvInventory);
        RecyclerView categoriesRecyclerView = view.findViewById(R.id.rvCategories);
        EditText etSearch = view.findViewById(R.id.etSearch);
        com.proconsi.electrobazar.utils.ThemeManager.applyColorsToView(view, requireContext());

        adapter = new InventoryProductAdapter();
        recyclerView.setAdapter(adapter);

        categoryAdapter = new CategoryAdapter(category -> {
            viewModel.filterByCategory(category.getId());
            categoryAdapter.setSelectedCategoryId(category.getId());
        });
        categoriesRecyclerView.setAdapter(categoryAdapter);

        swipeRefresh.setOnRefreshListener(() -> viewModel.loadProducts());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterBySearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        observeViewModel();
        
        viewModel.loadProducts();
        viewModel.loadCategories();

        return view;
    }

    private void observeViewModel() {
        viewModel.getFilteredProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            swipeRefresh.setRefreshing(loading);
        });

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.setCategories(categories);
        });
    }
}
