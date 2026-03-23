package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.ProductPriceResponse;
import java.util.ArrayList;
import java.util.List;

public class PriceHistoryAdapter extends RecyclerView.Adapter<PriceHistoryAdapter.ViewHolder> {

    private List<ProductPriceResponse> items = new ArrayList<>();

    public void setItems(List<ProductPriceResponse> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductPriceResponse p = items.get(position);
        holder.tvProductName.setText(p.getProductName() != null ? p.getProductName() : "Producto #" + p.getProductId());
        holder.tvPrice.setText(String.format("%.2f€", p.getPrice()));
        holder.tvDate.setText("Desde: " + (p.getStartDate() != null ? p.getStartDate().replace("T", " ") : "N/A"));
        holder.tvLabel.setText(p.getLabel() != null ? p.getLabel() : "Cambio manual");
        
        if (p.getVatRate() != null) {
            holder.tvVat.setText("IVA: " + p.getVatRate().multiply(new java.math.BigDecimal("100")) + "%");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, tvDate, tvLabel, tvVat;

        ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvVat = itemView.findViewById(R.id.tvVat);
        }
    }
}
