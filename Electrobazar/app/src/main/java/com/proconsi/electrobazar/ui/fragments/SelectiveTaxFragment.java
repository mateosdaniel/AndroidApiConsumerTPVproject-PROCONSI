package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.ApplySelectiveTaxRateRequest;
import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.TaxRate;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.ui.adapters.SelectableAdminAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectiveTaxFragment extends Fragment {

    private AutoCompleteTextView autoTaxRate, autoCategoryFilter;
    private TabLayout tabSelection;
    private View layoutProducts, layoutCategories;
    private RecyclerView rvProducts, rvCategories;
    private CheckBox cbAllProducts, cbAllCategories;
    private Button btnApply;

    private SelectableAdminAdapter<Product> productAdapter;
    private SelectableAdminAdapter<Category> categoryAdapter;
    private ApiService apiService;
    
    private List<TaxRate> activeTaxRates = new ArrayList<>();
    private List<Category> allCategories = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private TaxRate selectedTaxRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selective_tax, container, false);
        apiService = RetrofitClient.getInstance().getApi();

        autoTaxRate = view.findViewById(R.id.autoTaxRate);
        autoCategoryFilter = view.findViewById(R.id.autoCategoryFilter);
        tabSelection = view.findViewById(R.id.tabSelection);
        layoutProducts = view.findViewById(R.id.layoutProducts);
        layoutCategories = view.findViewById(R.id.layoutCategories);
        rvProducts = view.findViewById(R.id.rvProducts);
        rvCategories = view.findViewById(R.id.rvCategories);
        cbAllProducts = view.findViewById(R.id.cbSelectAllProducts);
        cbAllCategories = view.findViewById(R.id.cbSelectAllCategories);
        btnApply = view.findViewById(R.id.btnApply);

        setupAdapters();
        setupListeners();
        loadInitialData();

        return view;
    }

    private void setupAdapters() {
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new SelectableAdminAdapter<>(new SelectableAdminAdapter.ItemBinder<Product>() {
            @Override
            public Long getId(Product item) { return item.getId(); }
            @Override
            public String getMainText(Product item) { return item.getName(); }
            @Override
            public String getSubText(Product item) { return item.getPrice() + "€"; }
        });
        rvProducts.setAdapter(productAdapter);

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new SelectableAdminAdapter<>(new SelectableAdminAdapter.ItemBinder<Category>() {
            @Override
            public Long getId(Category item) { return item.getId(); }
            @Override
            public String getMainText(Category item) { return item.getName(); }
            @Override
            public String getSubText(Category item) { return "Categoría"; }
        });
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupListeners() {
        tabSelection.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    layoutProducts.setVisibility(View.VISIBLE);
                    layoutCategories.setVisibility(View.GONE);
                } else {
                    layoutProducts.setVisibility(View.GONE);
                    layoutCategories.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        cbAllProducts.setOnCheckedChangeListener((bv, checked) -> productAdapter.selectAll(checked));
        cbAllCategories.setOnCheckedChangeListener((bv, checked) -> categoryAdapter.selectAll(checked));

        autoTaxRate.setOnItemClickListener((parent, view, position, id) -> {
            selectedTaxRate = activeTaxRates.get(position);
        });

        autoCategoryFilter.setOnItemClickListener((parent, view, position, id) -> {
            Category cat = allCategories.get(position);
            filterProductsByCategory(cat.getId());
        });

        btnApply.setOnClickListener(v -> confirmApply());
    }

    private void loadInitialData() {
        apiService.getActiveTaxRates().enqueue(new Callback<List<TaxRate>>() {
            @Override
            public void onResponse(Call<List<TaxRate>> call, Response<List<TaxRate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeTaxRates = response.body();
                    List<String> hints = new ArrayList<>();
                    for (TaxRate tr : activeTaxRates) hints.add(tr.getDescription());
                    autoTaxRate.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, hints));
                }
            }
            @Override
            public void onFailure(Call<List<TaxRate>> call, Throwable t) {}
        });

        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategories = response.body();
                    categoryAdapter.setItems(allCategories);
                    List<String> hints = new ArrayList<>();
                    for (Category c : allCategories) hints.add(c.getName());
                    autoCategoryFilter.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, hints));
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {}
        });

        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    productAdapter.setItems(allProducts);
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {}
        });
    }

    private void filterProductsByCategory(Long categoryId) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getCategory() != null && p.getCategory().getId().equals(categoryId)) {
                filtered.add(p);
            }
        }
        productAdapter.setItems(filtered);
    }

    private void confirmApply() {
        if (selectedTaxRate == null) {
            Toast.makeText(getContext(), "Selecciona un tipo de IVA", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Long> productIds = new ArrayList<>(productAdapter.getSelectedIds());
        List<Long> categoryIds = new ArrayList<>(categoryAdapter.getSelectedIds());

        if (productIds.isEmpty() && categoryIds.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona al menos un producto o categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setTitle("Aplicar IVA Selectivo")
                .setMessage("Vas a aplicar '" + selectedTaxRate.getDescription() + "' a los elementos seleccionados. ¿Continuar?")
                .setPositiveButton("Aplicar", (dialog, which) -> applySelective(productIds, categoryIds))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void applySelective(List<Long> productIds, List<Long> categoryIds) {
        ApplySelectiveTaxRateRequest request = new ApplySelectiveTaxRateRequest();
        request.setTaxRateId(selectedTaxRate.getId());
        request.setProductIds(productIds);
        request.setCategoryIds(categoryIds);

        apiService.applySelectiveTaxRate(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "IVA aplicado correctamente", Toast.LENGTH_SHORT).show();
                    // Reset selection
                    productAdapter.selectAll(false);
                    categoryAdapter.selectAll(false);
                } else {
                    Toast.makeText(getContext(), "Error al aplicar IVA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
