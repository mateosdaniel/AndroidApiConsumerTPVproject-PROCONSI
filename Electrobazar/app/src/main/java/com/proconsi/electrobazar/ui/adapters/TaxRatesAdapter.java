package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.TaxRate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TaxRatesAdapter extends RecyclerView.Adapter<TaxRatesAdapter.ViewHolder> {

    private List<TaxRate> taxRates = new ArrayList<>();
    private OnTaxRateActionListener listener;

    public interface OnTaxRateActionListener {
        void onEdit(TaxRate taxRate);
        void onDelete(TaxRate taxRate);
    }

    public TaxRatesAdapter(OnTaxRateActionListener listener) {
        this.listener = listener;
    }

    public void setTaxRates(List<TaxRate> taxRates) {
        this.taxRates = taxRates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tax_rate_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaxRate rate = taxRates.get(position);
        holder.tvDescription.setText(rate.getDescription());
        
        BigDecimal vatPercent = rate.getVatRate().multiply(new BigDecimal("100"));
        holder.tvVatRate.setText(vatPercent.stripTrailingZeros().toPlainString() + "%");
        
        BigDecimal rePercent = rate.getReRate().multiply(new BigDecimal("100"));
        holder.tvReRate.setText(rePercent.stripTrailingZeros().toPlainString() + "%");
        
        String dateRange = (rate.getValidFrom() != null ? rate.getValidFrom() : "...") + " - " + 
                           (rate.getValidTo() != null ? rate.getValidTo() : "Presente");
        holder.tvDateRange.setText(dateRange);

        if (Boolean.TRUE.equals(rate.getActive())) {
            holder.badgeStatus.setText("ACTIVO");
            holder.badgeStatus.setBackgroundResource(R.drawable.bg_active_badge);
        } else {
            holder.badgeStatus.setText("INACTIVO");
            holder.badgeStatus.setBackgroundResource(R.drawable.bg_inactive_badge);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(rate));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(rate));
    }

    @Override
    public int getItemCount() {
        return taxRates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvVatRate, tvReRate, tvDateRange, badgeStatus;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvVatRate = itemView.findViewById(R.id.tvVatRate);
            tvReRate = itemView.findViewById(R.id.tvReRate);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            badgeStatus = itemView.findViewById(R.id.badgeStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
