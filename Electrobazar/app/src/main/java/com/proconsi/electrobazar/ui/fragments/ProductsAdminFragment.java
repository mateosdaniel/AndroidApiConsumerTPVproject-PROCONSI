package com.proconsi.electrobazar.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.ProductRequest;
import com.proconsi.electrobazar.models.TaxRate;
import com.proconsi.electrobazar.ui.adapters.AdminProductAdapter;
import com.proconsi.electrobazar.ui.adapters.CategoryAdapter;
import com.proconsi.electrobazar.viewmodels.ProductsViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductsAdminFragment extends Fragment {

    private ProductsViewModel viewModel;
    private AdminProductAdapter adapter;
    private CategoryAdapter categoryAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private List<Category> availableCategories = new ArrayList<>();
    private List<TaxRate> availableTaxRates = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products_admin, container, false);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(view, requireContext());

        viewModel = new ViewModelProvider(this).get(ProductsViewModel.class);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = view.findViewById(R.id.rvProductsAdmin);
        RecyclerView categoriesRecyclerView = view.findViewById(R.id.rvCategories);
        EditText etSearch = view.findViewById(R.id.etSearch);
        View fabAdd = view.findViewById(R.id.fabAddProduct);
        com.proconsi.electrobazar.utils.ThemeManager.applyColorsToView(view, requireContext());

        adapter = new AdminProductAdapter(new AdminProductAdapter.OnAdminProductActionListener() {
            @Override
            public void onEdit(Product product) {
                showProductDialog(product);
            }

            @Override
            public void onDelete(Product product) {
                showDeleteConfirmation(product);
            }
        });
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

        fabAdd.setOnClickListener(v -> showProductDialog(null));

        observeViewModel();

        viewModel.loadProducts();
        viewModel.loadCategories();
        viewModel.loadTaxRates();

        return view;
    }

    private void observeViewModel() {
        viewModel.getFilteredProducts().observe(getViewLifecycleOwner(), products -> adapter.setProducts(products));
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> swipeRefresh.setRefreshing(loading));
        
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            availableCategories = categories;
            categoryAdapter.setCategories(categories);
        });
        
        viewModel.getTaxRates().observe(getViewLifecycleOwner(), rates -> availableTaxRates = rates);

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearMessages();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });
    }

    private void showProductDialog(@Nullable Product product) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_product_admin, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);
        EditText etStock = dialogView.findViewById(R.id.etStock);
        EditText etImageUrl = dialogView.findViewById(R.id.etImageUrl);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
        Spinner spinnerTaxRate = dialogView.findViewById(R.id.spinnerTaxRate);
        SwitchMaterial switchActive = dialogView.findViewById(R.id.switchActive);
        View btnSave = dialogView.findViewById(R.id.btnSave);

        // Setup Spinners
        List<String> catNames = new ArrayList<>();
        for (Category c : availableCategories) catNames.add(c.getName());
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, catNames);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        List<String> taxNames = new ArrayList<>();
        for (TaxRate t : availableTaxRates) taxNames.add(t.getDescription() + " (" + (t.getVatRate().multiply(new BigDecimal("100"))) + "%)");
        ArrayAdapter<String> taxAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, taxNames);
        taxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaxRate.setAdapter(taxAdapter);

        if (product != null) {
            tvTitle.setText("Editar Producto");
            etName.setText(product.getName());
            etDescription.setText(product.getDescription());
            etPrice.setText(product.getPrice().toString());
            etStock.setText(product.getStock().toString());
            etImageUrl.setText(product.getImageUrl());
            switchActive.setChecked(product.getActive() != null && product.getActive());
            
            // Set selection for spinners
            if (product.getCategory() != null) {
                for (int i = 0; i < availableCategories.size(); i++) {
                    if (availableCategories.get(i).getId().equals(product.getCategory().getId())) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
            if (product.getTaxRate() != null) {
                for (int i = 0; i < availableTaxRates.size(); i++) {
                    if (availableTaxRates.get(i).getId().equals(product.getTaxRate().getId())) {
                        spinnerTaxRate.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            tvTitle.setText("Nuevo Producto");
        }

        btnSave.setOnClickListener(v -> {
            ProductRequest request = new ProductRequest();
            request.setName(etName.getText().toString());
            request.setDescription(etDescription.getText().toString());
            try {
                request.setPrice(new BigDecimal(etPrice.getText().toString()));
                request.setStock(Integer.parseInt(etStock.getText().toString()));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Precio o stock inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            request.setImageUrl(etImageUrl.getText().toString());
            request.setActive(switchActive.isChecked());
            
            if (spinnerCategory.getSelectedItemPosition() >= 0) {
                request.setCategoryId(availableCategories.get(spinnerCategory.getSelectedItemPosition()).getId());
            }
            if (spinnerTaxRate.getSelectedItemPosition() >= 0) {
                request.setTaxRateId(availableTaxRates.get(spinnerTaxRate.getSelectedItemPosition()).getId());
            }

            if (product == null) {
                viewModel.createProduct(request);
            } else {
                viewModel.updateProduct(product.getId(), request);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteConfirmation(Product product) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Producto")
                .setMessage("¿Qué desea hacer con " + product.getName() + "?")
                .setPositiveButton("Desactivar (Soft)", (d, w) -> viewModel.deleteProduct(product.getId()))
                .setNeutralButton("Eliminar Definitivamente (Hard)", (d, w) -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("CONFIRMACIÓN EXTRA")
                            .setMessage("Esta acción es irreversible y podría afectar a ventas pasadas. ¿Seguro?")
                            .setPositiveButton("SÍ, ELIMINAR", (d2, w2) -> viewModel.hardDeleteProduct(product.getId()))
                            .setNegativeButton("CANCELAR", null)
                            .show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
