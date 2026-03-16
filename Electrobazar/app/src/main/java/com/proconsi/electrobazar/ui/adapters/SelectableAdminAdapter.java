package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proconsi.electrobazar.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectableAdminAdapter<T> extends RecyclerView.Adapter<SelectableAdminAdapter.ViewHolder> {

    private List<T> items = new ArrayList<>();
    private Set<Long> selectedIds = new HashSet<>();
    private ItemBinder<T> binder;

    public interface ItemBinder<T> {
        Long getId(T item);
        String getMainText(T item);
        String getSubText(T item);
    }

    public SelectableAdminAdapter(ItemBinder<T> binder) {
        this.binder = binder;
    }

    public void setItems(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void selectAll(boolean select) {
        selectedIds.clear();
        if (select) {
            for (T item : items) {
                selectedIds.add(binder.getId(item));
            }
        }
        notifyDataSetChanged();
    }

    public Set<Long> getSelectedIds() {
        return new HashSet<>(selectedIds);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selectable_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = items.get(position);
        Long id = binder.getId(item);
        holder.tvMain.setText(binder.getMainText(item));
        holder.tvSub.setText(binder.getSubText(item));
        
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedIds.contains(id));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedIds.add(id);
            else selectedIds.remove(id);
        });

        holder.itemView.setOnClickListener(v -> holder.checkBox.toggle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvMain, tvSub;

        ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            tvMain = itemView.findViewById(R.id.tvMainText);
            tvSub = itemView.findViewById(R.id.tvSubText);
        }
    }
}
