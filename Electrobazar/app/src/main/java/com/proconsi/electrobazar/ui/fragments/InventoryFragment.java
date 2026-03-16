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
import com.proconsi.electrobazar.ui.adapters.InventoryProductAdapter;
import com.proconsi.electrobazar.viewmodels.ProductsViewModel;

public class InventoryFragment extends Fragment {

    private ProductsViewModel viewModel;
    private InventoryProductAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ChipGroup chipGroupCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(view, requireContext());
        
        viewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);
        RecyclerView recyclerView = view.findViewById(R.id.rvInventory);
        EditText etSearch = view.findViewById(R.id.etSearch);

        adapter = new InventoryProductAdapter();
        recyclerView.setAdapter(adapter);

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
            chipGroupCategories.removeAllViews();
            
            // "All" chip
            Chip allChip = new Chip(getContext());
            allChip.setText("Todos");
            allChip.setCheckable(true);
            allChip.setChecked(true);
            allChip.setOnClickListener(v -> viewModel.filterByCategory(null));
            chipGroupCategories.addView(allChip);

            for (Category category : categories) {
                Chip chip = new Chip(getContext());
                chip.setText(category.getName());
                chip.setCheckable(true);
                chip.setOnClickListener(v -> viewModel.filterByCategory(category.getId()));
                chipGroupCategories.addView(chip);
            }
        });
    }
}
