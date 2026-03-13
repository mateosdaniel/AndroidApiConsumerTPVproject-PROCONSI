package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Category;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private final OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    public AdminCategoryAdapter(OnCategoryActionListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categories.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvActiveStatus;
        View btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvActiveStatus = itemView.findViewById(R.id.tvActiveStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Category category, OnCategoryActionListener listener) {
            tvName.setText(category.getName());
            tvDescription.setText(category.getDescription() != null ? category.getDescription() : "Sin descripción");
            
            boolean active = category.getActive() != null ? category.getActive() : false;
            if (active) {
                tvActiveStatus.setText("ACTIVA");
                tvActiveStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.success));
            } else {
                tvActiveStatus.setText("INACTIVA");
                tvActiveStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.danger));
            }

            btnEdit.setOnClickListener(v -> listener.onEdit(category));
            btnDelete.setOnClickListener(v -> listener.onDelete(category));
        }
    }
}
