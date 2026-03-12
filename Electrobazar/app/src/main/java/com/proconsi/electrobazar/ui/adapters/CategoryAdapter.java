package com.proconsi.electrobazar.ui.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private Long selectedCategoryId = null;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categoryList) {
        this.categories = new ArrayList<>();
        // Add "Todos" option
        Category all = new Category();
        all.setId(null);
        all.setName("Todos");
        this.categories.add(all);
        
        if (categoryList != null) {
            this.categories.addAll(categoryList);
        }
        notifyDataSetChanged();
    }

    public void setSelectedCategoryId(Long id) {
        this.selectedCategoryId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position), selectedCategoryId, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton categoryButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryButton = itemView.findViewById(R.id.categoryButton);
        }

        public void bind(final Category category, Long selectedId, final OnCategoryClickListener listener) {
            categoryButton.setText(category.getName());
            
            boolean isSelected = (selectedId == null && category.getId() == null) || 
                                 (selectedId != null && category.getId() != null && selectedId.equals(category.getId()));

            if (isSelected) {
                categoryButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.accent)));
                categoryButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.primary));
            } else {
                categoryButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.secondary)));
                categoryButton.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_main));
            }
            
            categoryButton.setOnClickListener(v -> listener.onCategoryClick(category));
        }
    }
}
