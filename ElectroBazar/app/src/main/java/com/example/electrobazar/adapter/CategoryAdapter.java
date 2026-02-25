package com.example.electrobazar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electrobazar.R;
import com.example.electrobazar.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final Context context;
    private final List<Category> categories;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0; // "Todos" seleccionado por defecto

    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());

        boolean isSelected = position == selectedPosition;

        // Color del texto
        holder.categoryName.setTextColor(ContextCompat.getColor(context,
                isSelected ? R.color.accent_cyan : R.color.dark_text_muted));

        // Borde izquierdo activo
        holder.activeBorder.setBackgroundColor(isSelected
                ? ContextCompat.getColor(context, R.color.accent_cyan)
                : android.graphics.Color.TRANSPARENT);

        // Fondo activo
        holder.itemView.setBackgroundColor(isSelected
                ? ContextCompat.getColor(context, R.color.dark_primary)
                : android.graphics.Color.TRANSPARENT);

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        View activeBorder;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            activeBorder = itemView.findViewById(R.id.categoryActiveBorder);
        }
    }
}