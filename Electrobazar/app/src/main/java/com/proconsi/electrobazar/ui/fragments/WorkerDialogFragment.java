package com.proconsi.electrobazar.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Role;
import com.proconsi.electrobazar.models.Worker;

import java.util.ArrayList;
import java.util.List;

public class WorkerDialogFragment extends DialogFragment {

    private Worker worker;
    private List<Role> roles = new ArrayList<>();
    private OnWorkerSavedListener listener;

    private TextInputEditText usernameEdit, passwordEdit;
    private AutoCompleteTextView roleDropdown;
    private TextInputLayout passwordLayout, roleLayout;
    private SwitchMaterial activeSwitch;
    private TextView dialogTitle;

    public interface OnWorkerSavedListener {
        void onWorkerSaved(Worker worker);
    }

    public static WorkerDialogFragment newInstance(Worker worker, List<Role> roles, OnWorkerSavedListener listener) {
        WorkerDialogFragment fragment = new WorkerDialogFragment();
        fragment.worker = worker;
        fragment.roles = roles;
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_worker_form, container, false);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(view, requireContext());
        com.proconsi.electrobazar.utils.ThemeManager.applyColorsToView(view, requireContext());

        dialogTitle = view.findViewById(R.id.dialogTitle);
        usernameEdit = view.findViewById(R.id.editWorkerUsername);
        passwordEdit = view.findViewById(R.id.editWorkerPassword);
        passwordLayout = view.findViewById(R.id.workerPasswordLayout);
        roleDropdown = view.findViewById(R.id.editWorkerRoleSpinner);
        roleLayout = view.findViewById(R.id.workerRoleLayout);
        activeSwitch = view.findViewById(R.id.editWorkerActiveSwitch);

        setupRoleDropdown();

        if (worker != null) {
            dialogTitle.setText("Editar Trabajador");
            usernameEdit.setText(worker.getUsername());
            activeSwitch.setChecked(worker.isActive());
            passwordLayout.setHint("Contraseña (dejar vacío para mantener)");

            // Select current role
            if (worker.getRole() != null) {
                roleDropdown.setText(worker.getRole().getName(), false);
            }
        } else {
            dialogTitle.setText("Nuevo Trabajador");
        }

        view.findViewById(R.id.cancelWorkerBtn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.saveWorkerBtn).setOnClickListener(v -> saveWorker());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void setupRoleDropdown() {
        List<String> roleNames = new ArrayList<>();
        for (Role r : roles) {
            roleNames.add(r.getName());
        }
        // Use simple_list_item_1 so it respects the current theme text colors
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, roleNames);
        roleDropdown.setAdapter(adapter);
    }

    private void saveWorker() {
        String username = usernameEdit.getText() != null ? usernameEdit.getText().toString().trim() : "";
        String password = passwordEdit.getText() != null ? passwordEdit.getText().toString().trim() : "";
        boolean active = activeSwitch.isChecked();
        String selectedRoleName = roleDropdown.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(getContext(), "El nombre de usuario es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (worker == null && password.isEmpty()) {
            Toast.makeText(getContext(), "La contraseña es obligatoria para nuevos trabajadores", Toast.LENGTH_SHORT).show();
            return;
        }

        Role selectedRole = null;
        for (Role r : roles) {
            if (r.getName().equals(selectedRoleName)) {
                selectedRole = r;
                break;
            }
        }

        if (worker == null) {
            worker = new Worker();
        }

        worker.setUsername(username);
        if (!password.isEmpty()) {
            worker.setPassword(password);
        }
        worker.setRole(selectedRole);
        worker.setActive(active);

        listener.onWorkerSaved(worker);
        dismiss();
    }
}
