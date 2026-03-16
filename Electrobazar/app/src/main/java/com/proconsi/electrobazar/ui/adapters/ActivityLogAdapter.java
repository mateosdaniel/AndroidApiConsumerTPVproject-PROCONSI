package com.proconsi.electrobazar.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.databinding.ItemActivityLogBinding;
import com.proconsi.electrobazar.models.ActivityLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ViewHolder> {

    private List<ActivityLog> fullList = new ArrayList<>();
    private List<ActivityLog> filteredList = new ArrayList<>();

    public void setLogs(List<ActivityLog> logs) {
        this.fullList = logs;
        this.filteredList = new ArrayList<>(logs);
        notifyDataSetChanged();
    }

    public void filter(String user, String action) {
        filteredList.clear();
        for (ActivityLog log : fullList) {
            boolean userMatch = user.isEmpty() || (log.getUsername() != null && log.getUsername().toLowerCase().contains(user.toLowerCase()));
            boolean actionMatch = action.isEmpty() || (log.getAction() != null && log.getAction().toLowerCase().contains(action.toLowerCase()));
            
            if (userMatch && actionMatch) {
                filteredList.add(log);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActivityLogBinding binding = ItemActivityLogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityLog log = filteredList.get(position);
        holder.binding.tvActivityAction.setText(log.getAction());
        holder.binding.tvActivityDescription.setText(log.getDescription());
        holder.binding.tvActivityUser.setText(log.getUsername() != null ? log.getUsername() : "Anónimo");
        holder.binding.tvActivityTime.setText(log.getTimestamp()); // Simplification: expecting formatted string from API

        // Set indicator color based on action type
        String action = log.getAction() != null ? log.getAction() : "";
        if (action.contains("VENTA")) {
            holder.binding.activityIndicator.setBackgroundColor(Color.parseColor("#22c55e")); // Green
        } else if (action.contains("ELIMINAR") || action.contains("BORRAR") || action.contains("CANCELAR")) {
            holder.binding.activityIndicator.setBackgroundColor(Color.parseColor("#ef4444")); // Red
        } else if (action.contains("LOGIN") || action.contains("CONFIG")) {
            holder.binding.activityIndicator.setBackgroundColor(Color.parseColor("#3b82f6")); // Blue
        } else {
            holder.binding.activityIndicator.setBackgroundColor(Color.parseColor("#f5a623")); // Accent
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemActivityLogBinding binding;
        ViewHolder(ItemActivityLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
