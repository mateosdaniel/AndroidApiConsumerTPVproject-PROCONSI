package com.proconsi.electrobazar.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Role;
import com.proconsi.electrobazar.models.Worker;
import com.proconsi.electrobazar.repositories.WorkersAdminRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkerDialogFragment extends DialogFragment {

    private Worker worker;
    private List<Role> roles = new ArrayList<>();
    private OnWorkerSavedListener listener;

    private EditText usernameEdit, passwordEdit;
    private Spinner roleSpinner;
    private SwitchMaterial activeSwitch;
    private TextView passwordLabel, dialogTitle;

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

        dialogTitle = view.findViewById(R.id.dialogTitle);
        usernameEdit = view.findViewById(R.id.editWorkerUsername);
        passwordEdit = view.findViewById(R.id.editWorkerPassword);
        passwordLabel = view.findViewById(R.id.workerPasswordLabel);
        roleSpinner = view.findViewById(R.id.editWorkerRoleSpinner);
        activeSwitch = view.findViewById(R.id.editWorkerActiveSwitch);
        
        setupRoleSpinner();

        if (worker != null) {
            dialogTitle.setText("Editar Trabajador");
            usernameEdit.setText(worker.getUsername());
            activeSwitch.setChecked(worker.isActive());
            passwordLabel.setText("Contraseña (opcional, blanco para mantener)");
            
            // Select current role
            if (worker.getRole() != null) {
                for (int i = 0; i < roles.size(); i++) {
                    if (roles.get(i).getId().equals(worker.getRole().getId())) {
                        roleSpinner.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            dialogTitle.setText("Nuevo Trabajador");
            passwordLabel.setText("Contraseña *");
        }

        view.findViewById(R.id.cancelWorkerBtn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.saveWorkerBtn).setOnClickListener(v -> saveWorker());

        return view;
    }

    private void setupRoleSpinner() {
        List<String> roleNames = new ArrayList<>();
        for (Role r : roles) {
            roleNames.add(r.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roleNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
    }

    private void saveWorker() {
        String username = usernameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        boolean active = activeSwitch.isChecked();
        
        if (username.isEmpty()) {
            Toast.makeText(getContext(), "El nombre de usuario es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (worker == null && password.isEmpty()) {
            Toast.makeText(getContext(), "La contraseña es obligatoria para nuevos trabajadores", Toast.LENGTH_SHORT).show();
            return;
        }

        Role selectedRole = null;
        if (roleSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION) {
            selectedRole = roles.get(roleSpinner.getSelectedItemPosition());
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
