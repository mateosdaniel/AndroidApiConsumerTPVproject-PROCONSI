package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.CashRegister;

import java.math.BigDecimal;
import java.util.List;

public class CashClosureAdapter extends RecyclerView.Adapter<CashClosureAdapter.ViewHolder> {

    public interface OnPdfDownloadListener {
        void onDownload(CashRegister closure);
    }

    private final List<CashRegister> closures;
    private final OnPdfDownloadListener listener;

    public CashClosureAdapter(List<CashRegister> closures, OnPdfDownloadListener listener) {
        this.closures = closures;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cash_closure_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CashRegister closure = closures.get(position);
        
        holder.idText.setText("#" + closure.getId());
        holder.dateText.setText(closure.getRegisterDate());
        holder.workerText.setText("Trabajador: " + (closure.getWorker() != null ? closure.getWorker().getUsername() : "N/A"));
        
        holder.openingText.setText(String.format("%.2f€", closure.getOpeningBalance()));
        holder.salesText.setText(String.format("%.2f€", (closure.getTotalSales() != null ? closure.getTotalSales() : BigDecimal.ZERO)));
        
        BigDecimal diff = closure.getDifference() != null ? closure.getDifference() : BigDecimal.ZERO;
        holder.diffText.setText(String.format("%.2f€", diff));
        if (diff.compareTo(BigDecimal.ZERO) < 0) {
            holder.diffText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.danger));
        } else if (diff.compareTo(BigDecimal.ZERO) > 0) {
            holder.diffText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.info));
        } else {
            holder.diffText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.success));
        }
        
        holder.closingText.setText(String.format("%.2f€", closure.getClosingBalance()));
        
        holder.btnDownload.setOnClickListener(v -> listener.onDownload(closure));
    }

    @Override
    public int getItemCount() {
        return closures.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idText, dateText, workerText, openingText, salesText, diffText, closingText;
        MaterialButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idText = itemView.findViewById(R.id.closureIdText);
            dateText = itemView.findViewById(R.id.closureDateText);
            workerText = itemView.findViewById(R.id.closureWorkerText);
            openingText = itemView.findViewById(R.id.openingText);
            salesText = itemView.findViewById(R.id.salesText);
            diffText = itemView.findViewById(R.id.diffText);
            closingText = itemView.findViewById(R.id.closingText);
            btnDownload = itemView.findViewById(R.id.btnDownloadPdf);
        }
    }
}
