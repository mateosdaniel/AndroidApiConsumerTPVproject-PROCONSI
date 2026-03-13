package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RolesAdapter extends RecyclerView.Adapter<RolesAdapter.RoleViewHolder> {

    private List<Role> roles = new ArrayList<>();
    private final OnRoleActionListener listener;

    public interface OnRoleActionListener {
        void onEdit(Role role);
        void onDelete(Role role);
    }

    public RolesAdapter(OnRoleActionListener listener) {
        this.listener = listener;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_role_admin, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        Role role = roles.get(position);
        holder.bind(role, listener);
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    static class RoleViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, workersCountText;
        ChipGroup permissionsGroup;
        ImageButton editBtn, deleteBtn;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.roleNameText);
            descriptionText = itemView.findViewById(R.id.roleDescriptionText);
            workersCountText = itemView.findViewById(R.id.roleWorkersCountText);
            permissionsGroup = itemView.findViewById(R.id.rolePermissionsBadgeGroup);
            editBtn = itemView.findViewById(R.id.editRoleBtn);
            deleteBtn = itemView.findViewById(R.id.deleteRoleBtn);
        }

        public void bind(Role role, OnRoleActionListener listener) {
            nameText.setText(role.getName());
            descriptionText.setText(role.getDescription() != null && !role.getDescription().isEmpty() ? role.getDescription() : "Sin descripción");
            
            // To show worker count, we'd need workers data. 
            // In the web version it seems to be calculated or fetched. 
            // Let's assume description or a placeholder for now if it's not in the Role model.
            // Actually, Role model usually doesn't have worker count. 
            // AdminController.java line 80: model.addAttribute("roles", roleService.findAll());
            // RoleService.findAll() returns List<Role>. 
            // I'll check Worker model to see if it links back. 
            // For now, I'll set a generic message or hide if not available.
            workersCountText.setVisibility(View.GONE); 

            permissionsGroup.removeAllViews();
            Set<String> perms = role.getPermissions();
            if (perms != null && !perms.isEmpty()) {
                for (String p : perms) {
                    Chip chip = new Chip(itemView.getContext());
                    chip.setText(p);
                    chip.setChipMinHeight(0f);
                    chip.setChipStartPadding(8f);
                    chip.setChipEndPadding(8f);
                    chip.setTextSize(10f);
                    chip.setClickable(false);
                    // Minimal styling similar to web
                    chip.setChipBackgroundColorResource(R.color.border);
                    chip.setTextColor(itemView.getContext().getColor(R.color.text_muted));
                    permissionsGroup.addView(chip);
                }
            } else {
                TextView tv = new TextView(itemView.getContext());
                tv.setText("Sin permisos");
                tv.setTextSize(12);
                tv.setTextColor(itemView.getContext().getColor(R.color.text_muted));
                permissionsGroup.addView(tv);
            }

            editBtn.setOnClickListener(v -> listener.onEdit(role));
            deleteBtn.setOnClickListener(v -> listener.onDelete(role));
        }
    }
}
