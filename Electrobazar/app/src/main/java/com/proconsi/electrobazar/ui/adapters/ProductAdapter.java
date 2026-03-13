package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final ImageView productPlaceholder;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView productCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productPlaceholder = itemView.findViewById(R.id.productPlaceholder);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productCategory = itemView.findViewById(R.id.productCategory);
        }

        public void bind(final Product product, final OnProductClickListener listener) {
            productName.setText(product.getName());
            productPrice.setText(String.format(Locale.getDefault(), "%.2f€", product.getPrice()));

            if (product.getCategory() != null) {
                productCategory.setText(product.getCategory().getName());
                productCategory.setVisibility(View.VISIBLE);
            } else {
                productCategory.setVisibility(View.GONE);
            }

            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                productPlaceholder.setVisibility(View.GONE);
                productImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .centerCrop()
                        .into(productImage);
            } else {
                productImage.setVisibility(View.GONE);
                productPlaceholder.setVisibility(View.VISIBLE);
            }

            // Stock check: disable cards with no stock
            int stock = product.getStock() != null ? product.getStock() : 0;
            if (stock <= 0) {
                itemView.setAlpha(0.4f);
                itemView.setOnClickListener(null);
                itemView.setClickable(false);
            } else {
                itemView.setAlpha(1.0f);
                itemView.setClickable(true);
                itemView.setOnClickListener(v -> {
                    // Scale animation
                    v.animate()
                        .scaleX(1.05f)
                        .scaleY(1.05f)
                        .setDuration(75)
                        .withEndAction(() -> {
                            v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(75)
                                .start();
                            listener.onProductClick(product);
                        })
                        .start();
                });
            }
        }
    }
}
