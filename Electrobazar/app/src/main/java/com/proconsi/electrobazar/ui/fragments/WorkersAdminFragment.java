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

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Role;
import com.proconsi.electrobazar.models.Worker;
import com.proconsi.electrobazar.repositories.WorkersAdminRepository;
import com.proconsi.electrobazar.ui.adapters.WorkersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorkersAdminFragment extends Fragment implements WorkersAdapter.OnWorkerActionListener {

    private WorkersAdminRepository repository;
    private WorkersAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private EditText searchInput;
    private ChipGroup roleFilterChips, statusFilterChips;
    private TextView resultsCountText;
    
    private List<Worker> allWorkers = new ArrayList<>();
    private List<Role> allRoles = new ArrayList<>();
    
    private String currentSearchQuery = "";
    private Long selectedRoleId = null;
    private Boolean selectedActiveStatus = null; // null = All, true = Active, false = Inactive

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workers_admin, container, false);
        
        repository = new WorkersAdminRepository();
        
        swipeRefresh = view.findViewById(R.id.swipeRefreshWorkers);
        searchInput = view.findViewById(R.id.workerSearchInput);
        roleFilterChips = view.findViewById(R.id.roleFilterChipGroup);
        statusFilterChips = view.findViewById(R.id.statusFilterChipGroup);
        resultsCountText = view.findViewById(R.id.workerResultsCountText);
        RecyclerView recyclerView = view.findViewById(R.id.workersRecyclerView);
        FloatingActionButton addFab = view.findViewById(R.id.addWorkerFab);

        adapter = new WorkersAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        setupListeners();
        loadData();

        addFab.setOnClickListener(v -> showWorkerDialog(null));

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

        roleFilterChips.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipRoleAll || checkedId == View.NO_ID) {
                selectedRoleId = null;
            } else {
                Chip chip = group.findViewById(checkedId);
                if (chip != null && chip.getTag() != null) {
                    selectedRoleId = (Long) chip.getTag();
                } else {
                    selectedRoleId = null;
                }
            }
            applyFilters();
        });

        statusFilterChips.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipStatusAll || checkedId == View.NO_ID) {
                selectedActiveStatus = null;
            } else if (checkedId == R.id.chipStatusActive) {
                selectedActiveStatus = true;
            } else if (checkedId == R.id.chipStatusInactive) {
                selectedActiveStatus = false;
            }
            applyFilters();
        });
    }

    private void loadData() {
        swipeRefresh.setRefreshing(true);
        repository.getRoles(new WorkersAdminRepository.OnResultListener<List<Role>>() {
            @Override
            public void onSuccess(List<Role> roles) {
                allRoles = roles;
                updateRoleChips();
                loadWorkers();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error cargando roles: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWorkers() {
        repository.getWorkers(new WorkersAdminRepository.OnResultListener<List<Worker>>() {
            @Override
            public void onSuccess(List<Worker> workers) {
                allWorkers = workers;
                applyFilters();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error cargando trabajadores: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRoleChips() {
        // Keep the "All" chip
        int childCount = roleFilterChips.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = roleFilterChips.getChildAt(i);
            if (child.getId() != R.id.chipRoleAll) {
                roleFilterChips.removeView(child);
            }
        }

        for (Role role : allRoles) {
            Chip chip = new Chip(requireContext());
            chip.setText(role.getName());
            chip.setCheckable(true);
            chip.setTag(role.getId());
            chip.setChipBackgroundColorResource(R.color.chip_bg_selector);
            chip.setTextColor(requireContext().getColorStateList(R.color.chip_text_selector));
            // Set style manually since Chip(context, style) is not directly available like this
            roleFilterChips.addView(chip);
        }
    }

    private void applyFilters() {
        List<Worker> filtered = allWorkers.stream()
            .filter(w -> w.getUsername().toLowerCase().contains(currentSearchQuery))
            .filter(w -> selectedRoleId == null || (w.getRole() != null && w.getRole().getId().equals(selectedRoleId)))
            .filter(w -> selectedActiveStatus == null || w.isActive() == selectedActiveStatus)
            .collect(Collectors.toList());
        
        adapter.setWorkers(filtered);
        resultsCountText.setText("Mostrando " + filtered.size() + " trabajadores.");
    }

    private void showWorkerDialog(@Nullable Worker worker) {
        WorkerDialogFragment dialog = WorkerDialogFragment.newInstance(worker, allRoles, w -> {
            if (w.getId() == null) {
                createWorker(w);
            } else {
                updateWorker(w);
            }
        });
        dialog.show(getChildFragmentManager(), "WorkerDialog");
    }

    private void createWorker(Worker worker) {
        swipeRefresh.setRefreshing(true);
        repository.createWorker(worker, new WorkersAdminRepository.OnResultListener<Worker>() {
            @Override
            public void onSuccess(Worker result) {
                Toast.makeText(getContext(), "Trabajador creado con éxito", Toast.LENGTH_SHORT).show();
                loadWorkers();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error al crear: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWorker(Worker worker) {
        swipeRefresh.setRefreshing(true);
        repository.updateWorker(worker.getId(), worker, new WorkersAdminRepository.OnResultListener<Worker>() {
            @Override
            public void onSuccess(Worker result) {
                Toast.makeText(getContext(), "Trabajador actualizado con éxito", Toast.LENGTH_SHORT).show();
                loadWorkers();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error al actualizar: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEdit(Worker worker) {
        showWorkerDialog(worker);
    }

    @Override
    public void onDelete(Worker worker) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_Electrobazar)
            .setTitle("Confirmar")
            .setMessage("¿Deseas desactivar al trabajador " + worker.getUsername() + "?")
            .setPositiveButton("DESACTIVAR", (dialog, which) -> {
                swipeRefresh.setRefreshing(true);
                repository.deactivateWorker(worker.getId(), new WorkersAdminRepository.OnResultListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(getContext(), "Trabajador desactivado", Toast.LENGTH_SHORT).show();
                        loadWorkers();
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
}
