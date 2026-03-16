package com.proconsi.electrobazar.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.BulkPriceUpdateRequest;
import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.models.IpcPreviewItem;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.ProductPriceResponse;
import com.proconsi.electrobazar.network.ApiClient;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.ui.adapters.SelectableAdminAdapter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BulkPricesFragment extends Fragment {

    private TextView tvCurrentIpc;
    private Button btnFetchIpc, btnApplyIpc, btnPreview, btnSubmit;
    private MaterialButtonToggleGroup toggleType;
    private EditText etAdjustmentValue, etEffectiveDate, etLabel;
    private AutoCompleteTextView autoCategory;
    private RecyclerView rvProducts;
    private CheckBox cbSelectAll;

    private ApiService apiService;
    private SelectableAdminAdapter<Product> productAdapter;
    private List<Category> allCategories = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private BigDecimal suggestedIpc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bulk_prices_admin, container, false);
        apiService = ApiClient.getApiService();

        tvCurrentIpc = view.findViewById(R.id.tvCurrentIpc);
        btnFetchIpc = view.findViewById(R.id.btnFetchIpc);
        btnApplyIpc = view.findViewById(R.id.btnApplyIpc);
        btnPreview = view.findViewById(R.id.btnPreview);
        btnSubmit = view.findViewById(R.id.btnSubmitBulk);
        toggleType = view.findViewById(R.id.toggleType);
        etAdjustmentValue = view.findViewById(R.id.etAdjustmentValue);
        etEffectiveDate = view.findViewById(R.id.etEffectiveDate);
        etLabel = view.findViewById(R.id.etLabel);
        autoCategory = view.findViewById(R.id.autoCategory);
        rvProducts = view.findViewById(R.id.rvVisibleProducts);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);

        setupAdapter();
        setupListeners();
        loadData();

        return view;
    }

    private void setupAdapter() {
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
    }

    private void setupListeners() {
        btnFetchIpc.setOnClickListener(v -> fetchIpc());
        btnApplyIpc.setOnClickListener(v -> {
            if (suggestedIpc != null) {
                toggleType.check(R.id.btnPercentage);
                etAdjustmentValue.setText(suggestedIpc.toPlainString());
            }
        });

        etEffectiveDate.setOnClickListener(v -> showDatePicker());
        
        autoCategory.setOnItemClickListener((parent, view, position, id) -> {
            Category cat = allCategories.get(position);
            filterProducts(cat.getId());
        });

        cbSelectAll.setOnCheckedChangeListener((bv, checked) -> productAdapter.selectAll(checked));

        btnPreview.setOnClickListener(v -> showPreview());
        btnSubmit.setOnClickListener(v -> confirmSubmit());
    }

    private void loadData() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategories = response.body();
                    List<String> names = new ArrayList<>();
                    for (Category c : allCategories) names.add(c.getName());
                    autoCategory.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, names));
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

    private void fetchIpc() {
        apiService.getCurrentIpc().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object val = response.body().get("ipcValue");
                    if (val != null) {
                        suggestedIpc = new BigDecimal(val.toString());
                        tvCurrentIpc.setText("Último IPC: " + suggestedIpc + "%");
                        btnApplyIpc.setEnabled(true);
                    }
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
        });
    }

    private void filterProducts(Long categoryId) {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getCategory() != null && p.getCategory().getId().equals(categoryId)) {
                filtered.add(p);
            }
        }
        productAdapter.setItems(filtered);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1); // Tomorrow by default
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            etEffectiveDate.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showPreview() {
        String valStr = etAdjustmentValue.getText().toString();
        if (valStr.isEmpty()) return;
        BigDecimal val = new BigDecimal(valStr);

        // Fetch preview from server for top products
        apiService.getIpcPreview(val).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showPreviewDialog(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
        });
    }

    private void showPreviewDialog(List<Map<String, Object>> previewData) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ejemplo de cambios (Top 5 productos):\n\n");
        for (Map<String, Object> item : previewData) {
            sb.append(item.get("productName")).append(":\n")
              .append(item.get("currentPrice")).append("€ -> ")
              .append(item.get("newPrice")).append("€\n\n");
        }

        new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setTitle("Previsualización")
                .setMessage(sb.toString())
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void confirmSubmit() {
        List<Long> selectedIds = new ArrayList<>(productAdapter.getSelectedIds());
        if (selectedIds.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona al menos un producto", Toast.LENGTH_SHORT).show();
            return;
        }

        String valStr = etAdjustmentValue.getText().toString();
        String dateStr = etEffectiveDate.getText().toString();
        if (valStr.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(getContext(), "Completa valor y fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setTitle("Programar Precios")
                .setMessage("¿Deseas programar este cambio para " + selectedIds.size() + " productos?")
                .setPositiveButton("Programar", (dialog, which) -> submitBulk(selectedIds, valStr, dateStr))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void submitBulk(List<Long> ids, String val, String date) {
        BulkPriceUpdateRequest request = new BulkPriceUpdateRequest();
        request.setProductIds(ids);
        request.setEffectiveDate(date + "T00:00:00");
        request.setLabel(etLabel.getText().toString());

        if (toggleType.getCheckedButtonId() == R.id.btnPercentage) {
            request.setPercentage(new BigDecimal(val));
        } else {
            request.setFixedAmount(new BigDecimal(val));
        }

        apiService.bulkSchedulePrices(request).enqueue(new Callback<List<ProductPriceResponse>>() {
            @Override
            public void onResponse(Call<List<ProductPriceResponse>> call, Response<List<ProductPriceResponse>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Precios programados correctamente", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Error al programar precios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductPriceResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
