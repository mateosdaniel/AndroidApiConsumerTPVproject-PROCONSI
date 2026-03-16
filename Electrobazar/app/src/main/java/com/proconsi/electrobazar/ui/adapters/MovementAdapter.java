package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.CashWithdrawal;

import java.util.List;

public class MovementAdapter extends RecyclerView.Adapter<MovementAdapter.ViewHolder> {

    private final List<CashWithdrawal> movements;

    public MovementAdapter(List<CashWithdrawal> movements) {
        this.movements = movements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cash_movement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CashWithdrawal movement = movements.get(position);
        
        boolean isEntry = "ENTRY".equals(movement.getType());
        holder.typeText.setText(isEntry ? "ENTRADA" : "RETIRADA");
        holder.typeText.setTextColor(holder.itemView.getContext().getResources().getColor(isEntry ? R.color.success : R.color.danger));
        
        holder.amountText.setText(String.format("%s%.2f€", isEntry ? "+" : "-", movement.getAmount()));
        holder.reasonText.setText(movement.getReason() != null ? movement.getReason() : "Sin concepto");
        holder.timeText.setText(movement.getCreatedAt()); // Backend should return formatted or we format here
    }

    @Override
    public int getItemCount() {
        return movements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeText, amountText, reasonText, timeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.movementTypeText);
            amountText = itemView.findViewById(R.id.movementAmountText);
            reasonText = itemView.findViewById(R.id.movementReasonText);
            timeText = itemView.findViewById(R.id.movementTimeText);
        }
    }
}
