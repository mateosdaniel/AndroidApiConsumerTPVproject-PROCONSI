package com.proconsi.electrobazar.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.ui.adapters.TaxRatesAdapter;
import com.proconsi.electrobazar.models.TaxRate;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.network.ApiService;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaxRatesAdminFragment extends Fragment {

    private RecyclerView rvTaxRates;
    private TaxRatesAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ApiService apiService;
    private FloatingActionButton fabAdd;
    private Button btnApplyGlobally;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tax_rates_admin, container, false);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(view, requireContext());
        apiService = RetrofitClient.getInstance().getApi();
        
        rvTaxRates = view.findViewById(R.id.rvTaxRates);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        fabAdd = view.findViewById(R.id.fabAddTaxRate);
        btnApplyGlobally = view.findViewById(R.id.btnApplyGlobally);
        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

        rvTaxRates.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaxRatesAdapter(new TaxRatesAdapter.OnTaxRateActionListener() {
            @Override
            public void onEdit(TaxRate taxRate) {
                showTaxRateDialog(taxRate);
            }

            @Override
            public void onDelete(TaxRate taxRate) {
                confirmDelete(taxRate);
            }
        });
        rvTaxRates.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadTaxRates);
        fabAdd.setOnClickListener(v -> showTaxRateDialog(null));
        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());
        btnApplyGlobally.setOnClickListener(v -> confirmApplyGlobally());

        loadTaxRates();
        return view;
    }

    private void loadTaxRates() {
        swipeRefresh.setRefreshing(true);
        apiService.getTaxRates().enqueue(new Callback<List<TaxRate>>() {
            @Override
            public void onResponse(Call<List<TaxRate>> call, Response<List<TaxRate>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setTaxRates(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al cargar tipos de IVA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TaxRate>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTaxRateDialog(@Nullable TaxRate rate) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_tax_rate_edit, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setView(dialogView)
                .create();

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etVatRate = dialogView.findViewById(R.id.etVatRate);
        EditText etReRate = dialogView.findViewById(R.id.etReRate);
        EditText etValidFrom = dialogView.findViewById(R.id.etValidFrom);
        EditText etValidTo = dialogView.findViewById(R.id.etValidTo);
        SwitchMaterial switchActive = dialogView.findViewById(R.id.switchActive);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        if (rate != null) {
            tvTitle.setText("Editar Tipo de IVA");
            etDescription.setText(rate.getDescription());
            etVatRate.setText(rate.getVatRate().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());
            etReRate.setText(rate.getReRate().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());
            etValidFrom.setText(rate.getValidFrom());
            etValidTo.setText(rate.getValidTo());
            switchActive.setChecked(Boolean.TRUE.equals(rate.getActive()));
        } else {
            tvTitle.setText("Nuevo Tipo de IVA");
        }

        etValidFrom.setOnClickListener(v -> showDatePicker(etValidFrom));
        etValidTo.setOnClickListener(v -> showDatePicker(etValidTo));

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String desc = etDescription.getText().toString();
            String vatStr = etVatRate.getText().toString();
            String reStr = etReRate.getText().toString();

            if (desc.isEmpty() || vatStr.isEmpty() || reStr.isEmpty()) {
                Toast.makeText(getContext(), "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            TaxRate newRate = rate != null ? rate : new TaxRate();
            newRate.setDescription(desc);
            newRate.setVatRate(new BigDecimal(vatStr).divide(new BigDecimal("100")));
            newRate.setReRate(new BigDecimal(reStr).divide(new BigDecimal("100")));
            newRate.setValidFrom(etValidFrom.getText().toString());
            newRate.setValidTo(etValidTo.getText().toString());
            newRate.setActive(switchActive.isChecked());

            saveTaxRate(newRate, dialog);
        });

        dialog.show();
        
        // Fix for narrow dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        // Apply theme colors and fonts to the dialog
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(dialogView, requireContext());
        com.proconsi.electrobazar.utils.ThemeManager.applyColorsToView(dialogView, requireContext());
    }

    private void showDatePicker(EditText et) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            et.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveTaxRate(TaxRate rate, AlertDialog dialog) {
        Call<TaxRate> call = (rate.getId() == null) ? 
                apiService.createTaxRate(rate) : apiService.updateTaxRate(rate.getId(), rate);

        call.enqueue(new Callback<TaxRate>() {
            @Override
            public void onResponse(Call<TaxRate> call, Response<TaxRate> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Guardado correctamente", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadTaxRates();
                } else {
                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaxRate> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(TaxRate rate) {
        new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setTitle("Eliminar Tipo de IVA")
                .setMessage("¿Estás seguro de que deseas eliminar '" + rate.getDescription() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    apiService.deleteTaxRate(rate.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                                loadTaxRates();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmApplyGlobally() {
        // First, check which rates are available
        apiService.getActiveTaxRates().enqueue(new Callback<List<TaxRate>>() {
            @Override
            public void onResponse(Call<List<TaxRate>> call, Response<List<TaxRate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showApplyGloballyPicker(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<TaxRate>> call, Throwable t) {}
        });
    }

    private void showApplyGloballyPicker(List<TaxRate> rates) {
        String[] descriptions = new String[rates.size()];
        for (int i = 0; i < rates.size(); i++) descriptions[i] = rates.get(i).getDescription();

        new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setTitle("Aplicar a TODOS los productos")
                .setItems(descriptions, (dialog, which) -> {
                    TaxRate selected = rates.get(which);
                    applyGlobally(selected);
                })
                .show();
    }

    private void applyGlobally(TaxRate rate) {
        new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog)
                .setTitle("PELIGRO: Operación Global")
                .setMessage("Esto cambiará el IVA de TODOS los productos del sistema a '" + rate.getDescription() + "'. ¿Deseas continuar?")
                .setPositiveButton("SÍ, APLICAR A TODO", (dialog, which) -> {
                    apiService.applyTaxRateToProducts(rate.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Proceso iniciado correctamente", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
