package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Tariff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TariffsAdapter extends RecyclerView.Adapter<TariffsAdapter.ViewHolder> {

    private List<Tariff> tariffs = new ArrayList<>();
    private Map<Long, Long> customerCounts = new HashMap<>();
    private final OnTariffInteractionListener listener;

    public interface OnTariffInteractionListener {
        void onEdit(Tariff tariff);
        void onToggleStatus(Tariff tariff);
        void onDownloadPdf(Tariff tariff);
    }

    public TariffsAdapter(OnTariffInteractionListener listener) {
        this.listener = listener;
    }

    public void setTariffs(List<Tariff> tariffs) {
        this.tariffs = tariffs != null ? tariffs : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCustomerCounts(Map<Long, Long> counts) {
        this.customerCounts = counts != null ? counts : new HashMap<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tariff_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tariff tariff = tariffs.get(position);
        
        holder.tariffName.setText(tariff.getName());
        holder.tariffDiscount.setText(String.format(Locale.getDefault(), "%.2f%%", tariff.getDiscountPercentage() != null ? tariff.getDiscountPercentage().doubleValue() : 0.0));
        holder.tariffDescription.setText(tariff.getDescription() != null && !tariff.getDescription().isEmpty() ? tariff.getDescription() : "Sin descripción");
        
        boolean isActive = Boolean.TRUE.equals(tariff.getActive());
        holder.tariffStatus.setText(isActive ? "ACTIVA" : "INACTIVA");
        holder.tariffStatus.setBackgroundResource(isActive ? R.drawable.bg_badge : R.drawable.bg_badge_tariff); // Reusing badge backgrounds
        
        Long count = customerCounts.get(tariff.getId());
        long cCount = count != null ? count : 0;
        holder.tariffStats.setText(cCount + (cCount == 1 ? " cliente asociado" : " clientes asociados"));

        holder.btnEditTariff.setOnClickListener(v -> listener.onEdit(tariff));
        holder.btnToggleStatus.setOnClickListener(v -> listener.onToggleStatus(tariff));
        holder.btnDownloadPdf.setOnClickListener(v -> listener.onDownloadPdf(tariff));
        
        // System tariffs cannot be deactivated or edited name (though service only allows updating description/discount)
        if (Boolean.TRUE.equals(tariff.getSystemTariff())) {
            holder.btnToggleStatus.setVisibility(View.GONE);
        } else {
            holder.btnToggleStatus.setVisibility(View.VISIBLE);
            holder.btnToggleStatus.setImageResource(isActive ? R.drawable.ic_close : R.drawable.ic_plus);
        }
    }

    @Override
    public int getItemCount() {
        return tariffs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tariffName, tariffDiscount, tariffDescription, tariffStatus, tariffStats;
        ImageButton btnEditTariff, btnToggleStatus, btnDownloadPdf;

        ViewHolder(View itemView) {
            super(itemView);
            tariffName = itemView.findViewById(R.id.tariffName);
            tariffDiscount = itemView.findViewById(R.id.tariffDiscount);
            tariffDescription = itemView.findViewById(R.id.tariffDescription);
            tariffStatus = itemView.findViewById(R.id.tariffStatus);
            tariffStats = itemView.findViewById(R.id.tariffStats);
            btnEditTariff = itemView.findViewById(R.id.btnEditTariff);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
            btnDownloadPdf = itemView.findViewById(R.id.btnDownloadPdf);
        }
    }
}
