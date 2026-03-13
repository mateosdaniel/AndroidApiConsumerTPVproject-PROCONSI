package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoleDialogFragment extends DialogFragment {

    private Role role;
    private List<String> availablePermissions = new ArrayList<>();
    private OnRoleSavedListener listener;

    private EditText nameEdit, descriptionEdit;
    private LinearLayout permissionsContainer;
    private ProgressBar loadingProgress;
    private List<CheckBox> checkBoxes = new ArrayList<>();

    public interface OnRoleSavedListener {
        void onRoleSaved(Role role);
    }

    public static RoleDialogFragment newInstance(Role role, List<String> availablePermissions, OnRoleSavedListener listener) {
        RoleDialogFragment fragment = new RoleDialogFragment();
        fragment.role = role;
        fragment.availablePermissions = availablePermissions;
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
        View view = inflater.inflate(R.layout.dialog_role_form, container, false);

        nameEdit = view.findViewById(R.id.editRoleName);
        descriptionEdit = view.findViewById(R.id.editRoleDescription);
        permissionsContainer = view.findViewById(R.id.rolePermissionsFormContainer);
        loadingProgress = view.findViewById(R.id.permsLoadingProgress);

        setupPermissionsList();

        if (role != null) {
            ((TextView)view.findViewById(R.id.roleDialogTitle)).setText("Editar Rol");
            nameEdit.setText(role.getName());
            descriptionEdit.setText(role.getDescription());
            
            Set<String> perms = role.getPermissions();
            if (perms != null) {
                for (CheckBox cb : checkBoxes) {
                    if (perms.contains(cb.getText().toString())) {
                        cb.setChecked(true);
                    }
                }
            }
        }

        view.findViewById(R.id.cancelRoleBtn).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.saveRoleBtn).setOnClickListener(v -> saveRole());

        return view;
    }

    private void setupPermissionsList() {
        loadingProgress.setVisibility(View.GONE);
        permissionsContainer.removeAllViews();
        checkBoxes.clear();

        for (String p : availablePermissions) {
            CheckBox cb = new CheckBox(requireContext());
            cb.setText(p);
            cb.setTextColor(requireContext().getColor(R.color.text_main));
            
            if ("ADMIN_ACCESS".equals(p)) {
                cb.setTextColor(requireContext().getColor(R.color.danger));
                // Add a divider before special perms like in web? 
                // In Android we can just style it differently.
            }
            
            permissionsContainer.addView(cb);
            checkBoxes.add(cb);
        }
        
        if (availablePermissions.isEmpty()) {
            TextView tv = new TextView(requireContext());
            tv.setText("No se encontraron permisos disponibles.");
            tv.setTextColor(requireContext().getColor(R.color.text_muted));
            permissionsContainer.addView(tv);
        }
    }

    private void saveRole() {
        String name = nameEdit.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<String> selectedPerms = new HashSet<>();
        for (CheckBox cb : checkBoxes) {
            if (cb.isChecked()) {
                selectedPerms.add(cb.getText().toString());
            }
        }

        Role newRole = new Role();
        if (role != null) newRole.setId(role.getId());
        newRole.setName(name);
        newRole.setDescription(descriptionEdit.getText().toString().trim());
        newRole.setPermissions(selectedPerms);

        listener.onRoleSaved(newRole);
        dismiss();
    }
}
