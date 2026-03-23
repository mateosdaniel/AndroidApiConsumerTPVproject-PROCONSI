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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Category;
import com.proconsi.electrobazar.ui.adapters.AdminCategoryAdapter;
import com.proconsi.electrobazar.viewmodels.CategoriesViewModel;

public class CategoriesAdminFragment extends Fragment {

    private CategoriesViewModel viewModel;
    private AdminCategoryAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories_admin, container, false);

        viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = view.findViewById(R.id.rvCategories);
        EditText etSearch = view.findViewById(R.id.etSearch);
        View fabAdd = view.findViewById(R.id.fabAddCategory);

        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

        adapter = new AdminCategoryAdapter(new AdminCategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                showCategoryDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                showDeleteConfirmation(category);
            }
        });
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(() -> viewModel.loadCategories());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterBySearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        fabAdd.setOnClickListener(v -> showCategoryDialog(null));

        observeViewModel();

        viewModel.loadCategories();

        view.findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void observeViewModel() {
        viewModel.getFilteredCategories().observe(getViewLifecycleOwner(), categories -> adapter.setCategories(categories));
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> swipeRefresh.setRefreshing(loading));
        
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

    private void showCategoryDialog(@Nullable Category category) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_category_admin, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        SwitchMaterial switchActive = dialogView.findViewById(R.id.switchActive);
        View btnSave = dialogView.findViewById(R.id.btnSave);

        if (category != null) {
            tvTitle.setText("Editar Categoría");
            etName.setText(category.getName());
            etDescription.setText(category.getDescription());
            switchActive.setChecked(category.getActive() != null && category.getActive());
        } else {
            tvTitle.setText("Nueva Categoría");
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            Category request = new Category();
            request.setName(name);
            request.setDescription(etDescription.getText().toString().trim());
            request.setActive(switchActive.isChecked());

            if (category == null) {
                viewModel.createCategory(request);
            } else {
                request.setId(category.getId());
                viewModel.updateCategory(category.getId(), request);
            }
            dialog.dismiss();
        });

        dialog.show();
        
        // Ensure dialog matches parent width and theme
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(dialogView, requireContext());
            com.proconsi.electrobazar.utils.ThemeManager.applyColorsToView(dialogView, requireContext());
        }
    }

    private void showDeleteConfirmation(Category category) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Categoría")
                .setMessage("¿Seguro que desea eliminar la categoría " + category.getName() + "?")
                .setPositiveButton("ELIMINAR", (d, w) -> viewModel.deleteCategory(category.getId()))
                .setNegativeButton("CANCELAR", null)
                .show();
    }
}
