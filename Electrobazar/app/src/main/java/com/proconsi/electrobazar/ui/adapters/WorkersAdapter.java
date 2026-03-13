package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Worker;

import java.util.ArrayList;
import java.util.List;

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.WorkerViewHolder> {

    private List<Worker> workers = new ArrayList<>();
    private final OnWorkerActionListener listener;

    public interface OnWorkerActionListener {
        void onEdit(Worker worker);
        void onDelete(Worker worker);
    }

    public WorkersAdapter(OnWorkerActionListener listener) {
        this.listener = listener;
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worker_admin, parent, false);
        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        Worker worker = workers.get(position);
        holder.bind(worker, listener);
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }

    static class WorkerViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, roleBadge, statusBadge;
        ImageButton editBtn, deleteBtn;

        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.workerNameText);
            roleBadge = itemView.findViewById(R.id.workerRoleBadge);
            statusBadge = itemView.findViewById(R.id.workerStatusBadge);
            editBtn = itemView.findViewById(R.id.editWorkerBtn);
            deleteBtn = itemView.findViewById(R.id.deleteWorkerBtn);
        }

        public void bind(Worker worker, OnWorkerActionListener listener) {
            nameText.setText(worker.getUsername());
            
            if (worker.getRole() != null) {
                roleBadge.setText(worker.getRole().getName());
                roleBadge.setVisibility(View.VISIBLE);
            } else {
                roleBadge.setVisibility(View.GONE);
            }

            if (worker.isActive()) {
                statusBadge.setText("ACTIVO");
                statusBadge.setBackgroundResource(R.drawable.badge_active_yes);
            } else {
                statusBadge.setText("INACTIVO");
                statusBadge.setBackgroundResource(R.drawable.badge_active_no);
            }

            editBtn.setOnClickListener(v -> listener.onEdit(worker));
            deleteBtn.setOnClickListener(v -> listener.onDelete(worker));
        }
    }
}
