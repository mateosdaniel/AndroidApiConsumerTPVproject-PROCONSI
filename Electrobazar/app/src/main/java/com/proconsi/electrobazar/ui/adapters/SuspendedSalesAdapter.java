package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.SuspendedSaleResponse;
import java.util.ArrayList;
import java.util.List;

public class SuspendedSalesAdapter extends RecyclerView.Adapter<SuspendedSalesAdapter.ViewHolder> {

    private List<SuspendedSaleResponse> sales = new ArrayList<>();
    private final OnSuspendedSaleActionListener listener;

    public interface OnSuspendedSaleActionListener {
        void onResume(SuspendedSaleResponse sale);
        void onCancel(SuspendedSaleResponse sale);
    }

    public SuspendedSalesAdapter(OnSuspendedSaleActionListener listener) {
        this.listener = listener;
    }

    public void setSales(List<SuspendedSaleResponse> sales) {
        this.sales = sales;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suspended_sale, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SuspendedSaleResponse s = sales.get(position);
        holder.tvLabel.setText(s.getLabel() != null && !s.getLabel().isEmpty() ? s.getLabel() : "Venta #" + s.getId());
        holder.tvInfo.setText("Por: " + s.getWorkerUsername() + " • " + s.getCreatedAt().replace("T", " "));
        
        int items = 0;
        if (s.getLines() != null) {
            for (SuspendedSaleResponse.SuspendedSaleLineResponse line : s.getLines()) {
                items += line.getQuantity();
            }
        }
        holder.tvItems.setText(items + (items == 1 ? " producto" : " productos"));

        holder.btnResume.setOnClickListener(v -> listener.onResume(s));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(s));
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvInfo, tvItems;
        View btnResume, btnCancel;

        ViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvSuspendedLabel);
            tvInfo = itemView.findViewById(R.id.tvSuspendedInfo);
            tvItems = itemView.findViewById(R.id.tvSuspendedItems);
            btnResume = itemView.findViewById(R.id.btnResumeSuspended);
            btnCancel = itemView.findViewById(R.id.btnCancelSuspended);
        }
    }
}
