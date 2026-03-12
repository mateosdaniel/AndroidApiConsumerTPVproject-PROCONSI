package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.TicketLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<TicketLine> lines = new ArrayList<>();
    private final OnTicketLineInteractionListener listener;

    public interface OnTicketLineInteractionListener {
        void onIncreaseQty(TicketLine line);
        void onDecreaseQty(TicketLine line);
        void onRemoveLine(TicketLine line);
    }

    public TicketAdapter(OnTicketLineInteractionListener listener) {
        this.listener = listener;
    }

    public void setLines(List<TicketLine> lines) {
        this.lines = lines != null ? lines : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_line, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        holder.bind(lines.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        private final TextView lineName, lineQty, lineTotal;
        private final ImageButton btnPlus, btnMinus, btnRemove;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            lineName = itemView.findViewById(R.id.lineName);
            lineQty = itemView.findViewById(R.id.lineQty);
            lineTotal = itemView.findViewById(R.id.lineTotal);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(final TicketLine line, final OnTicketLineInteractionListener listener) {
            lineName.setText(line.getProduct().getName());
            lineQty.setText(String.valueOf(line.getQuantity()));
            lineTotal.setText(String.format(Locale.getDefault(), "%.2f€", line.getLineTotal()));

            btnPlus.setOnClickListener(v -> listener.onIncreaseQty(line));
            btnMinus.setOnClickListener(v -> listener.onDecreaseQty(line));
            btnRemove.setOnClickListener(v -> listener.onRemoveLine(line));
        }
    }
}
