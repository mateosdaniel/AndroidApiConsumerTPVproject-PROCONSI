package com.example.electrobazar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.electrobazar.R;
import com.example.electrobazar.models.Product;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    public AdminProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(String.format("%.2f €", product.getPrice()));
        holder.tvProductStock.setText("Stock: " + product.getStock());

        if (product.getCategory() != null) {
            holder.tvProductDesc.setText(product.getCategory().getName());
        } else {
            holder.tvProductDesc.setText("Sin categoría");
        }

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_search)
                    .into(holder.ivProductIcon);
        } else {
            holder.ivProductIcon.setImageResource(R.drawable.ic_search); // placeholder
        }
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductIcon;
        TextView tvProductName;
        TextView tvProductDesc;
        TextView tvProductPrice;
        TextView tvProductStock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductIcon = itemView.findViewById(R.id.ivProductIcon);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductStock = itemView.findViewById(R.id.tvProductStock);
        }
    }
}
