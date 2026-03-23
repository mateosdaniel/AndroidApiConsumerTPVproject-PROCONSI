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
import com.proconsi.electrobazar.repositories.RolesAdminRepository;
import com.proconsi.electrobazar.ui.adapters.RolesAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RolesAdminFragment extends Fragment implements RolesAdapter.OnRoleActionListener {

    private RolesAdminRepository repository;
    private RolesAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private EditText searchInput;
    private ChipGroup permissionFilterChips;
    private TextView resultsCountText;

    private List<Role> allRoles = new ArrayList<>();
    private List<String> availablePermissions = new ArrayList<>();
    private Set<String> filteredPermissions = new HashSet<>();

    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roles_admin, container, false);

        repository = new RolesAdminRepository();

        swipeRefresh = view.findViewById(R.id.swipeRefreshRoles);
        searchInput = view.findViewById(R.id.roleSearchInput);
        permissionFilterChips = view.findViewById(R.id.permissionFilterChipGroup);
        resultsCountText = view.findViewById(R.id.roleResultsCountText);
        RecyclerView recyclerView = view.findViewById(R.id.rolesRecyclerView);
        FloatingActionButton addFab = view.findViewById(R.id.addRoleFab);

        adapter = new RolesAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        setupListeners();
        loadData();

        addFab.setOnClickListener(v -> showRoleDialog(null));
        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

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
    }

    private void loadData() {
        swipeRefresh.setRefreshing(true);
        repository.getPermissions(new RolesAdminRepository.OnResultListener<List<String>>() {
            @Override
            public void onSuccess(List<String> permissions) {
                availablePermissions = permissions;
                setupFilterChips();
                loadRoles();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error cargando permisos: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilterChips() {
        permissionFilterChips.removeAllViews();
        filteredPermissions.clear();
        for (String p : availablePermissions) {
            Chip chip = new Chip(requireContext());
            chip.setText(p);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.chip_bg_selector);
            chip.setTextColor(requireContext().getColorStateList(R.color.chip_text_selector));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    filteredPermissions.add(p);
                } else {
                    filteredPermissions.remove(p);
                }
                applyFilters();
            });
            permissionFilterChips.addView(chip);
        }
    }

    private void loadRoles() {
        repository.getRoles(new RolesAdminRepository.OnResultListener<List<Role>>() {
            @Override
            public void onSuccess(List<Role> roles) {
                allRoles = roles;
                applyFilters();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error cargando roles: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        List<Role> filtered = allRoles.stream()
            .filter(r -> r.getName().toLowerCase().contains(currentSearchQuery))
            .filter(r -> {
                if (filteredPermissions.isEmpty()) return true;
                // Role must have ALL filtered permissions (like in web filter logic: every checked perm must be present)
                Set<String> rolePerms = r.getPermissions();
                return rolePerms != null && rolePerms.containsAll(filteredPermissions);
            })
            .collect(Collectors.toList());

        adapter.setRoles(filtered);
        resultsCountText.setText("Mostrando " + filtered.size() + " roles.");
    }

    private void showRoleDialog(@Nullable Role role) {
        RoleDialogFragment dialog = RoleDialogFragment.newInstance(role, availablePermissions, newRole -> {
            if (role == null) {
                createRole(newRole);
            } else {
                updateRole(role.getId(), newRole);
            }
        });
        dialog.show(getChildFragmentManager(), "RoleDialog");
    }

    private void createRole(Role role) {
        swipeRefresh.setRefreshing(true);
        repository.createRole(role, new RolesAdminRepository.OnResultListener<Role>() {
            @Override
            public void onSuccess(Role result) {
                Toast.makeText(getContext(), "Rol creado con éxito", Toast.LENGTH_SHORT).show();
                loadRoles();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error al crear: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRole(Long id, Role role) {
        swipeRefresh.setRefreshing(true);
        repository.updateRole(id, role, new RolesAdminRepository.OnResultListener<Role>() {
            @Override
            public void onSuccess(Role result) {
                Toast.makeText(getContext(), "Rol actualizado con éxito", Toast.LENGTH_SHORT).show();
                loadRoles();
            }

            @Override
            public void onError(String error) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error al actualizar: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEdit(Role role) {
        showRoleDialog(role);
    }

    @Override
    public void onDelete(Role role) {
        new AlertDialog.Builder(requireContext(), R.style.Theme_Electrobazar)
            .setTitle("Confirmar")
            .setMessage("¿Estás seguro de eliminar el rol " + role.getName() + "? Los trabajadores asignados perderán sus permisos.")
            .setPositiveButton("ELIMINAR", (dialog, which) -> {
                swipeRefresh.setRefreshing(true);
                repository.deleteRole(role.getId(), new RolesAdminRepository.OnResultListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(getContext(), "Rol eliminado", Toast.LENGTH_SHORT).show();
                        loadRoles();
                    }

                    @Override
                    public void onError(String error) {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(getContext(), "Error al eliminar: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("CANCELAR", null)
            .show();
    }
}
