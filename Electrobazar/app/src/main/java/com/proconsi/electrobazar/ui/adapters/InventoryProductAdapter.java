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

public class InventoryProductAdapter extends RecyclerView.Adapter<InventoryProductAdapter.ViewHolder> {

    private List<Product> products = new ArrayList<>();

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_product_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvCategory, tvStock, tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        void bind(Product product) {
            tvName.setText(product.getName());
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

            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_inventory)
                        .centerCrop()
                        .into(ivProduct);
            } else {
                ivProduct.setImageResource(R.drawable.ic_inventory);
            }
        }
    }
}
