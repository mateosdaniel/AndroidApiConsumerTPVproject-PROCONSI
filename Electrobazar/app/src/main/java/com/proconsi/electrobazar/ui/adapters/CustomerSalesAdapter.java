package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomerSalesAdapter extends RecyclerView.Adapter<CustomerSalesAdapter.ViewHolder> {

    private List<Map<String, Object>> sales = new ArrayList<>();
    private final OnSaleClickListener listener;

    public interface OnSaleClickListener {
        void onSaleClick(Map<String, Object> sale);
    }

    public CustomerSalesAdapter(OnSaleClickListener listener) {
        this.listener = listener;
    }

    public void setSales(List<Map<String, Object>> sales) {
        this.sales = sales != null ? sales : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> sale = sales.get(position);
        
        holder.idText.setText("#" + sale.get("id").toString());
        
        String dateStr = sale.get("createdAt").toString();
        holder.dateText.setText(dateStr.replace("T", " ").substring(0, 16));
        
        holder.customerText.setVisibility(View.GONE); // Hide customer name in this list as they all belong to same customer
        
        double totalAmount = Double.parseDouble(sale.get("totalAmount").toString());
        holder.totalText.setText(String.format(Locale.getDefault(), "%.2f €", totalAmount));
        
        holder.typeText.setText(sale.get("paymentMethod").toString());
        holder.methodText.setText(sale.get("status").toString());
        
        // Hide buttons for now
        holder.itemView.findViewById(R.id.btnDownload).setVisibility(View.GONE);
        holder.itemView.findViewById(R.id.btnCancel).setVisibility(View.GONE);
        holder.itemView.findViewById(R.id.btnEmail).setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> listener.onSaleClick(sale));
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idText, dateText, customerText, totalText, typeText, methodText;

        ViewHolder(View itemView) {
            super(itemView);
            idText = itemView.findViewById(R.id.invoiceIdText);
            dateText = itemView.findViewById(R.id.invoiceDateText);
            customerText = itemView.findViewById(R.id.invoiceCustomerText);
            totalText = itemView.findViewById(R.id.invoiceTotalText);
            typeText = itemView.findViewById(R.id.invoiceTypeText);
            methodText = itemView.findViewById(R.id.invoiceMethodText);
        }
    }
}
