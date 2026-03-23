package com.proconsi.electrobazar.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Tariff;
import com.proconsi.electrobazar.repositories.TariffsAdminRepository;
import com.proconsi.electrobazar.ui.adapters.TariffsAdapter;
import com.proconsi.electrobazar.utils.SessionManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class TariffsAdminFragment extends Fragment {

    private TariffsAdminRepository repository;
    private TariffsAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private View emptyState;
    private ProgressBar loadingProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tariffs_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new TariffsAdminRepository();
        initViews(view);
        loadData();
    }

    private void initViews(View view) {
        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());
        
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.tariffsRecyclerView);
        emptyState = view.findViewById(R.id.emptyState);
        loadingProgress = view.findViewById(R.id.loadingProgress);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TariffsAdapter(new TariffsAdapter.OnTariffInteractionListener() {
            @Override
            public void onEdit(Tariff tariff) {
                showTariffDialog(tariff);
            }

            @Override
            public void onToggleStatus(Tariff tariff) {
                toggleTariffStatus(tariff);
            }

            @Override
            public void onDownloadPdf(Tariff tariff) {
                downloadPdf(tariff);
            }
        });
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadData);
        swipeRefresh.setColorSchemeResources(R.color.accent);

        view.findViewById(R.id.fabAddTariff).setOnClickListener(v -> showTariffDialog(null));
    }

    private void loadData() {
        loadingProgress.setVisibility(View.VISIBLE);
        repository.getTariffs(true).observe(getViewLifecycleOwner(), tariffs -> {
            loadingProgress.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            if (tariffs != null) {
                adapter.setTariffs(tariffs);
                emptyState.setVisibility(tariffs.isEmpty() ? View.VISIBLE : View.GONE);
                
                // Fetch customer counts
                repository.getCustomerCounts().observe(getViewLifecycleOwner(), counts -> {
                    if (counts != null) {
                        adapter.setCustomerCounts(counts);
                    }
                });
            } else {
                Toast.makeText(getContext(), "Error al cargar tarifas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTariffDialog(Tariff tariff) {
        Dialog dialog = new Dialog(requireContext(), R.style.FullScreenDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tariff_edit);

        EditText nameInput = dialog.findViewById(R.id.tariffNameInput);
        EditText discountInput = dialog.findViewById(R.id.tariffDiscountInput);
        EditText descInput = dialog.findViewById(R.id.tariffDescriptionInput);
        SwitchCompat activeToggle = dialog.findViewById(R.id.tariffActiveToggle);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        TextView title = dialog.findViewById(R.id.dialogTitle);

        if (tariff != null) {
            title.setText("Editar Tarifa");
            nameInput.setText(tariff.getName());
            nameInput.setEnabled(false); // Backend doesn't support renaming via update
            discountInput.setText(String.valueOf(tariff.getDiscountPercentage()));
            descInput.setText(tariff.getDescription());
            activeToggle.setChecked(Boolean.TRUE.equals(tariff.getActive()));
            if (Boolean.TRUE.equals(tariff.getSystemTariff())) {
                activeToggle.setEnabled(false);
            }
        } else {
            title.setText("Nueva Tarifa");
            activeToggle.setChecked(true);
        }

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String discountStr = discountInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();

            if (name.isEmpty() || discountStr.isEmpty()) {
                Toast.makeText(getContext(), "Nombre y descuento son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            double discount = Double.parseDouble(discountStr);
            if (tariff == null) {
                repository.createTariff(name, discount, desc).observe(getViewLifecycleOwner(), t -> {
                    if (t != null) {
                        Toast.makeText(getContext(), "Tarifa creada", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadData();
                    } else {
                        Toast.makeText(getContext(), "Error al crear tarifa", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                repository.updateTariff(tariff.getId(), discount, desc).observe(getViewLifecycleOwner(), t -> {
                    if (t != null) {
                        // After update, if active status changed we need separate call or it might be handled in update? 
                        // Backend update endpoint only takes discount and description.
                        Toast.makeText(getContext(), "Tarifa actualizada", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadData();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar tarifa", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
        
        // Ensure dialog is MATCH_PARENT width and properly themed
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // Re-apply colors to the full window content including background
            com.proconsi.electrobazar.utils.ThemeManager.applyColorsToView(dialog.getWindow().getDecorView(), requireContext());
        }
    }

    private void toggleTariffStatus(Tariff tariff) {
        boolean activate = !Boolean.TRUE.equals(tariff.getActive());
        if (activate) {
            repository.activateTariff(tariff.getId()).observe(getViewLifecycleOwner(), success -> {
                if (Boolean.TRUE.equals(success)) {
                    Toast.makeText(getContext(), "Tarifa activada", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            });
        } else {
            new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle("Desactivar Tarifa")
                    .setMessage("¿Estás seguro? Los clientes asociados pasarán a la tarifa MINORISTA.")
                    .setPositiveButton("DESACTIVAR", (dialog, which) -> {
                        repository.deactivateTariff(tariff.getId()).observe(getViewLifecycleOwner(), success -> {
                            if (Boolean.TRUE.equals(success)) {
                                Toast.makeText(getContext(), "Tarifa desactivada", Toast.LENGTH_SHORT).show();
                                loadData();
                            }
                        });
                    })
                    .setNegativeButton("CANCELAR", null)
                    .show();
        }
    }

    private void downloadPdf(Tariff tariff) {
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        Toast.makeText(getContext(), "Iniciando descarga de '" + tariff.getName() + "'...", Toast.LENGTH_SHORT).show();
        
        com.proconsi.electrobazar.network.ApiService api = com.proconsi.electrobazar.network.RetrofitClient.getInstance().getApi();
        api.downloadTariffHistoryPdf(tariff.getId(), date).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.proconsi.electrobazar.utils.PdfUtils.saveAndOpenFile(getContext(), response.body(), "Tarifa_" + tariff.getName() + "_" + date + ".pdf");
                } else {
                    Toast.makeText(getContext(), "Error al obtener el PDF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
