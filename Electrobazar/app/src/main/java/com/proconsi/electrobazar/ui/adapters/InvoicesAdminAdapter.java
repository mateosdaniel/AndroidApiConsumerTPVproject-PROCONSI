package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.PaymentMethod;
import com.proconsi.electrobazar.models.Sale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InvoicesAdminAdapter extends RecyclerView.Adapter<InvoicesAdminAdapter.ViewHolder> {

    private List<Sale> sales = new ArrayList<>();
    private final OnSaleActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnSaleActionListener {
        void onDownloadInvoice(Sale sale);
        void onCancelSale(Sale sale);
        void onSaleClick(Sale sale);
    }

    public InvoicesAdminAdapter(OnSaleActionListener listener) {
        this.listener = listener;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
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
        Sale sale = sales.get(position);
        
        holder.idText.setText("#" + sale.getId());
        
        if (sale.getCreatedAt() != null) {
            try {
                // Assuming ISO format from backend: 2026-03-20T14:30:00
                String dateStr = sale.getCreatedAt();
                // Simple regex or parse
                holder.dateText.setText(dateStr.replace("T", " ").substring(0, 16));
            } catch (Exception e) {
                holder.dateText.setText(sale.getCreatedAt());
            }
        }
        
        String customerName = "Consumidor Final";
        if (sale.getCustomer() != null && sale.getCustomer().getName() != null) {
            customerName = sale.getCustomer().getName();
        }
        holder.customerText.setText(customerName);
        
        holder.totalText.setText(String.format(Locale.getDefault(), "%.2f €", sale.getTotalAmount()));
        
        boolean isInvoice = sale.getInvoice() != null;
        holder.typeText.setText(isInvoice ? "FACTURA" : "TICKET");
        holder.typeText.setBackgroundResource(isInvoice ? R.drawable.badge_bg_ticket : R.drawable.bg_surface_rounded);
        
        holder.methodText.setText(sale.getPaymentMethod() == PaymentMethod.CASH ? "Efectivo" : "Tarjeta");
        
        if ("CANCELLED".equals(sale.getStatus())) {
            holder.statusText.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.statusText.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        }

        holder.btnDownload.setOnClickListener(v -> listener.onDownloadInvoice(sale));
        holder.btnCancel.setOnClickListener(v -> listener.onCancelSale(sale));
        holder.itemView.setOnClickListener(v -> listener.onSaleClick(sale));
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idText, dateText, customerText, totalText, typeText, methodText, statusText;
        MaterialButton btnDownload, btnCancel;

        ViewHolder(View itemView) {
            super(itemView);
            idText = itemView.findViewById(R.id.invoiceIdText);
            dateText = itemView.findViewById(R.id.invoiceDateText);
            customerText = itemView.findViewById(R.id.invoiceCustomerText);
            totalText = itemView.findViewById(R.id.invoiceTotalText);
            typeText = itemView.findViewById(R.id.invoiceTypeText);
            methodText = itemView.findViewById(R.id.invoiceMethodText);
            statusText = itemView.findViewById(R.id.invoiceStatusText);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
