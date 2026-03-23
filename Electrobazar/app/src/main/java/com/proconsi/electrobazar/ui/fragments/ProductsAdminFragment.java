package com.proconsi.electrobazar.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    private final ActivityResultLauncher<String[]> csvPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    processUploadedCsv(uri);
                }
            }
    );

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

        view.findViewById(R.id.fabUploadCsv).setOnClickListener(v -> {
            csvPickerLauncher.launch(new String[]{"text/comma-separated-values", "text/csv", "application/vnd.ms-excel"});
        });

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
        com.google.android.material.textfield.TextInputEditText etName = dialogView.findViewById(R.id.etName);
        com.google.android.material.textfield.TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        com.google.android.material.textfield.TextInputEditText etPrice = dialogView.findViewById(R.id.etPrice);
        com.google.android.material.textfield.TextInputEditText etStock = dialogView.findViewById(R.id.etStock);
        com.google.android.material.textfield.TextInputEditText etImageUrl = dialogView.findViewById(R.id.etImageUrl);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
        AutoCompleteTextView spinnerTaxRate = dialogView.findViewById(R.id.spinnerTaxRate);
        SwitchMaterial switchActive = dialogView.findViewById(R.id.switchActive);
        View btnSave = dialogView.findViewById(R.id.btnSave);

        // Setup Dropdowns (AutoCompleteTextView - fixed theme-aware text colors)
        List<String> catNames = new ArrayList<>();
        for (Category c : availableCategories) catNames.add(c.getName());
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, catNames);
        spinnerCategory.setAdapter(catAdapter);

        List<String> taxNames = new ArrayList<>();
        for (TaxRate t : availableTaxRates) taxNames.add(t.getDescription() + " (" + (t.getVatRate().multiply(new BigDecimal("100"))) + "%)");
        ArrayAdapter<String> taxAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, taxNames);
        spinnerTaxRate.setAdapter(taxAdapter);

        if (product != null) {
            tvTitle.setText("Editar Producto");
            etName.setText(product.getName());
            etDescription.setText(product.getDescription());
            etPrice.setText(product.getPrice().toString());
            etStock.setText(product.getStock().toString());
            etImageUrl.setText(product.getImageUrl());
            switchActive.setChecked(product.getActive() != null && product.getActive());
            
            // Pre-fill dropdowns when editing
            if (product.getCategory() != null) {
                spinnerCategory.setText(product.getCategory().getName(), false);
            }
            if (product.getTaxRate() != null) {
                String taxLabel = product.getTaxRate().getDescription() + " (" + (product.getTaxRate().getVatRate().multiply(new BigDecimal("100"))) + "%)";
                spinnerTaxRate.setText(taxLabel, false);
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
            
            // Find selected category and tax rate by name match
            String selectedCatName = spinnerCategory.getText().toString().trim();
            for (Category c : availableCategories) {
                if (c.getName().equals(selectedCatName)) {
                    request.setCategoryId(c.getId());
                    break;
                }
            }
            String selectedTaxLabel = spinnerTaxRate.getText().toString().trim();
            for (int i = 0; i < taxNames.size(); i++) {
                if (taxNames.get(i).equals(selectedTaxLabel)) {
                    request.setTaxRateId(availableTaxRates.get(i).getId());
                    break;
                }
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

    private void processUploadedCsv(Uri uri) {
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            if (is == null) return;
            byte[] bytes = readAllBytes(is);
            RequestBody body = RequestBody.create(MediaType.parse("text/csv"), bytes);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", "import.csv", body);
            viewModel.uploadCsv(part);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al leer archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
