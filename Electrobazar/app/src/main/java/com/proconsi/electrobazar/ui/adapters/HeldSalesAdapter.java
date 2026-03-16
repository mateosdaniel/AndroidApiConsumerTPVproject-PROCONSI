package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.SuspendedSaleResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HeldSalesAdapter extends RecyclerView.Adapter<HeldSalesAdapter.ViewHolder> {

    private List<SuspendedSaleResponse> heldSales = new ArrayList<>();
    private final OnHeldSaleInteractionListener listener;

    public interface OnHeldSaleInteractionListener {
        void onRecover(SuspendedSaleResponse heldSale);
        void onDelete(SuspendedSaleResponse heldSale);
    }

    public HeldSalesAdapter(OnHeldSaleInteractionListener listener) {
        this.listener = listener;
    }

    public void setHeldSales(List<SuspendedSaleResponse> sales) {
        this.heldSales = sales != null ? sales : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_held_sale, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SuspendedSaleResponse sale = heldSales.get(position);
        
        holder.heldSaleLabel.setText(sale.getLabel() != null && !sale.getLabel().isEmpty() ? sale.getLabel() : "Sin etiqueta");
        holder.heldSaleDate.setText(sale.getCreatedAt() != null ? sale.getCreatedAt() : "");
        holder.heldSaleWorker.setText("Trabajador: " + (sale.getWorkerUsername() != null ? sale.getWorkerUsername() : "Anónimo"));
        
        int lineCount = sale.getLines() != null ? sale.getLines().size() : 0;
        holder.heldSaleLineCount.setText(lineCount + (lineCount == 1 ? " producto" : " productos"));
        
        BigDecimal total = BigDecimal.ZERO;
        if (sale.getLines() != null) {
            for (SuspendedSaleResponse.SuspendedSaleLineResponse line : sale.getLines()) {
                if (line.getUnitPrice() != null && line.getQuantity() != null) {
                    total = total.add(line.getUnitPrice().multiply(new BigDecimal(line.getQuantity())));
                }
            }
        }
        holder.heldSaleTotal.setText(String.format(Locale.getDefault(), "%.2f€", total));

        holder.btnRecover.setOnClickListener(v -> listener.onRecover(sale));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(sale));
    }

    @Override
    public int getItemCount() {
        return heldSales.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView heldSaleLabel, heldSaleDate, heldSaleWorker, heldSaleLineCount, heldSaleTotal;
        View btnRecover, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            heldSaleLabel = itemView.findViewById(R.id.heldSaleLabel);
            heldSaleDate = itemView.findViewById(R.id.heldSaleDate);
            heldSaleWorker = itemView.findViewById(R.id.heldSaleWorker);
            heldSaleLineCount = itemView.findViewById(R.id.heldSaleLineCount);
            heldSaleTotal = itemView.findViewById(R.id.heldSaleTotal);
            btnRecover = itemView.findViewById(R.id.btnRecover);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
