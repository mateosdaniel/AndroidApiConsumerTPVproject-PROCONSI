package com.example.electrobazar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.electrobazar.R;
import com.example.electrobazar.models.SaleLine;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    public interface OnTicketChangeListener {
        void onQuantityChanged(SaleLine line, int delta);
    }

    private final Context context;
    private final List<SaleLine> ticketLines;
    private final OnTicketChangeListener listener;

    public TicketAdapter(Context context, List<SaleLine> ticketLines, OnTicketChangeListener listener) {
        this.context = context;
        this.ticketLines = ticketLines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket_line, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        SaleLine line = ticketLines.get(position);
        holder.tvItemName.setText(line.getProduct().getName());
        holder.tvQty.setText(String.valueOf(line.getQuantity()));
        holder.tvItemPrice.setText(String.format("%.2f€", line.getSubtotal()));

        holder.btnPlus.setOnClickListener(v -> listener.onQuantityChanged(line, 1));
        holder.btnMinus.setOnClickListener(v -> listener.onQuantityChanged(line, -1));
    }

    @Override
    public int getItemCount() {
        return ticketLines.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvQty, tvItemPrice;
        ImageButton btnPlus, btnMinus;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
