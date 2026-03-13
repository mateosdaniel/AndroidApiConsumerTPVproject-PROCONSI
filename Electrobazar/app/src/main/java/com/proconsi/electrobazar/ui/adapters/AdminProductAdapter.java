package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {

    private List<Product> products = new ArrayList<>();
    private final OnAdminProductActionListener listener;

    public interface OnAdminProductActionListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    public AdminProductAdapter(OnAdminProductActionListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(products.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvDescription, tvCategory, tvActiveStatus, tvStock, tvPrice;
        View btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvActiveStatus = itemView.findViewById(R.id.tvActiveStatus);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Product product, OnAdminProductActionListener listener) {
            tvName.setText(product.getName());
            tvDescription.setText(product.getDescription() != null ? product.getDescription() : "");
            tvCategory.setText(product.getCategory() != null ? product.getCategory().getName() : "Sin categoría");
            tvPrice.setText(String.format(Locale.getDefault(), "%.2f €", product.getPrice()));
            
            int stock = product.getStock() != null ? product.getStock() : 0;
            tvStock.setText(stock + " uds");
            
            if (stock <= 0) {
                tvStock.getBackground().setTint(ContextCompat.getColor(itemView.getContext(), R.color.danger));
            } else if (stock < 5) {
                tvStock.getBackground().setTint(ContextCompat.getColor(itemView.getContext(), R.color.warning));
            } else {
                tvStock.getBackground().setTint(ContextCompat.getColor(itemView.getContext(), R.color.success));
            }

            boolean active = product.getActive() != null ? product.getActive() : false;
            if (active) {
                tvActiveStatus.setText("ACTIVO");
                tvActiveStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.success));
            } else {
                tvActiveStatus.setText("INACTIVO");
                tvActiveStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.danger));
            }

            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_inventory)
                        .centerCrop()
                        .into(ivProduct);
            } else {
                ivProduct.setImageResource(R.drawable.ic_inventory);
            }

            btnEdit.setOnClickListener(v -> listener.onEdit(product));
            btnDelete.setOnClickListener(v -> listener.onDelete(product));
        }
    }
}
