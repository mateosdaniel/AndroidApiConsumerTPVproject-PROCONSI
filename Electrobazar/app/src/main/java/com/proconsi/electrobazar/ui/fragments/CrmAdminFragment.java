package com.proconsi.electrobazar.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Customer;
import com.proconsi.electrobazar.models.CustomerRequest;
import com.proconsi.electrobazar.models.Tariff;
import com.proconsi.electrobazar.repositories.CrmAdminRepository;
import com.proconsi.electrobazar.ui.adapters.CustomersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrmAdminFragment extends Fragment implements CustomersAdapter.OnCustomerActionListener {

    private CrmAdminRepository repository;
    private CustomersAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private EditText searchInput;
    private ChipGroup typeFilterChips, reFilterChips;
    private TextView resultsCountText;

    private List<Customer> allCustomers = new ArrayList<>();
    private List<Tariff> allTariffs = new ArrayList<>();

    private String currentSearchQuery = "";
    private String selectedType = null; // null = All, INDIVIDUAL, COMPANY
    private Boolean selectedRe = null; // null = All, true = Yes, false = No

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crm_admin, container, false);

        repository = new CrmAdminRepository();

        swipeRefresh = view.findViewById(R.id.swipeRefreshCrm);
        searchInput = view.findViewById(R.id.crmSearchInput);
        typeFilterChips = view.findViewById(R.id.crmTypeFilterChipGroup);
        reFilterChips = view.findViewById(R.id.crmReFilterChipGroup);
        resultsCountText = view.findViewById(R.id.crmResultsCountText);
        RecyclerView recyclerView = view.findViewById(R.id.crmRecyclerView);
        FloatingActionButton addFab = view.findViewById(R.id.addCustomerFab);

        adapter = new CustomersAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        setupListeners();
        loadData();

        addFab.setOnClickListener(v -> showCustomerDialog(null));

        return view;
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(this::loadData);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        typeFilterChips.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipTypeAll || checkedId == View.NO_ID) {
                selectedType = null;
            } else if (checkedId == R.id.chipTypeIndividual) {
                selectedType = "INDIVIDUAL";
            } else if (checkedId == R.id.chipTypeCompany) {
                selectedType = "COMPANY";
            }
            applyFilters();
        });

        reFilterChips.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipReAll || checkedId == View.NO_ID) {
                selectedRe = null;
            } else if (checkedId == R.id.chipReYes) {
                selectedRe = true;
            } else if (checkedId == R.id.chipReNo) {
                selectedRe = false;
            }
            applyFilters();
        });
    }

    private void loadData() {
        swipeRefresh.setRefreshing(true);
        repository.getTariffs(new CrmAdminRepository.OnResultListener<List<Tariff>>() {
            @Override
            public void onSuccess(List<Tariff> tariffs) {
                allTariffs = tariffs;
                loadCustomers();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error cargando tarifas: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCustomers() {
        repository.getCustomers(new CrmAdminRepository.OnResultListener<List<Customer>>() {
            @Override
            public void onSuccess(List<Customer> customers) {
                allCustomers = customers;
                applyFilters();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error cargando clientes: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        List<Customer> filtered = allCustomers.stream()
            .filter(c -> c.getName().toLowerCase().contains(currentSearchQuery) || 
                         (c.getTaxId() != null && c.getTaxId().toLowerCase().contains(currentSearchQuery)) ||
                         (c.getEmail() != null && c.getEmail().toLowerCase().contains(currentSearchQuery)))
            .filter(c -> selectedType == null || c.getType().equals(selectedType))
            .filter(c -> selectedRe == null || (c.getHasRecargoEquivalencia() != null && c.getHasRecargoEquivalencia() == selectedRe))
            .collect(Collectors.toList());

        adapter.setCustomers(filtered);
        resultsCountText.setText("Mostrando " + filtered.size() + " clientes.");
    }

    private void showCustomerDialog(@Nullable Customer customer) {
        CustomerDialogFragment dialog = CustomerDialogFragment.newInstance(customer, allTariffs, request -> {
            if (customer == null) {
                createCustomer(request);
            } else {
                updateCustomer(customer.getId(), request);
            }
        });
        dialog.show(getChildFragmentManager(), "CustomerDialog");
    }

    private void createCustomer(CustomerRequest request) {
        swipeRefresh.setRefreshing(true);
        repository.createCustomer(request, new CrmAdminRepository.OnResultListener<Customer>() {
            @Override
            public void onSuccess(Customer result) {
                Toast.makeText(getContext(), "Cliente creado con éxito", Toast.LENGTH_SHORT).show();
                loadCustomers();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error al crear: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCustomer(Long id, CustomerRequest request) {
        swipeRefresh.setRefreshing(true);
        repository.updateCustomer(id, request, new CrmAdminRepository.OnResultListener<Customer>() {
            @Override
            public void onSuccess(Customer result) {
                Toast.makeText(getContext(), "Cliente actualizado con éxito", Toast.LENGTH_SHORT).show();
                loadCustomers();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error al actualizar: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEdit(Customer customer) {
        showCustomerDialog(customer);
    }

    @Override
    public void onDelete(Customer customer) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_Electrobazar)
            .setTitle("Confirmar")
            .setMessage("¿Deseas desactivar al cliente " + customer.getName() + "?")
            .setPositiveButton("DESACTIVAR", (dialog, which) -> {
                swipeRefresh.setRefreshing(true);
                repository.deleteCustomer(customer.getId(), new CrmAdminRepository.OnResultListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(getContext(), "Cliente desactivado", Toast.LENGTH_SHORT).show();
                        loadCustomers();
                    }

                    @Override
                    public void onError(String error) {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(getContext(), "Error al desactivar: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("CANCELAR", null)
            .show();
    }

    @Override
    public void onViewHistory(Customer customer) {
        CustomerSalesFragment fragment = CustomerSalesFragment.newInstance(customer.getId(), customer.getName());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
