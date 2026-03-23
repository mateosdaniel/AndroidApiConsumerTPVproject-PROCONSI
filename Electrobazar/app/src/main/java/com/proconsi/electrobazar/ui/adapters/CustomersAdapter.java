package com.proconsi.electrobazar.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder> {

    private List<Customer> customers = new ArrayList<>();
    private final OnCustomerActionListener listener;

    public interface OnCustomerActionListener {
        void onEdit(Customer customer);
        void onDelete(Customer customer);
        void onViewHistory(Customer customer);
    }

    public CustomersAdapter(OnCustomerActionListener listener) {
        this.listener = listener;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_admin, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer, listener);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, taxIdText, typeBadge, tariffBadge, reBadge, emailText, phoneText, cityText;
        ImageButton editBtn, deleteBtn, historyBtn;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.customerNameText);
            taxIdText = itemView.findViewById(R.id.customerTaxIdText);
            typeBadge = itemView.findViewById(R.id.customerTypeBadge);
            tariffBadge = itemView.findViewById(R.id.customerTariffBadge);
            reBadge = itemView.findViewById(R.id.customerReBadge);
            emailText = itemView.findViewById(R.id.customerEmailText);
            phoneText = itemView.findViewById(R.id.customerPhoneText);
            cityText = itemView.findViewById(R.id.customerCityText);
            editBtn = itemView.findViewById(R.id.editCustomerBtn);
            deleteBtn = itemView.findViewById(R.id.deleteCustomerBtn);
            historyBtn = itemView.findViewById(R.id.historyCustomerBtn);
        }

        public void bind(Customer customer, OnCustomerActionListener listener) {
            nameText.setText(customer.getName());
            taxIdText.setText(customer.getTaxId() != null && !customer.getTaxId().isEmpty() ? customer.getTaxId() : "—");
            
            if ("COMPANY".equals(customer.getType())) {
                typeBadge.setText("EMPRESA");
                typeBadge.setBackgroundResource(R.drawable.badge_bg_ticket); // colorAccent background
                typeBadge.setTextColor(com.proconsi.electrobazar.utils.ThemeManager.getOnAccentColor(typeBadge.getContext()));
            } else {
                typeBadge.setText("PARTICULAR");
                typeBadge.setBackgroundResource(R.drawable.bg_category_badge); // colorSurfaceVariant background
                typeBadge.setTextColor(com.proconsi.electrobazar.utils.ThemeManager.getTextColor(typeBadge.getContext()));
            }

            if (customer.getTariff() != null) {
                tariffBadge.setText(customer.getTariff().getName());
                tariffBadge.setBackgroundResource(R.drawable.bg_badge_tariff);
            } else {
                tariffBadge.setText("MINORISTA");
                tariffBadge.setBackgroundResource(R.drawable.bg_badge); // Default
            }

            if (Boolean.TRUE.equals(customer.getHasRecargoEquivalencia())) {
                reBadge.setVisibility(View.VISIBLE);
            } else {
                reBadge.setVisibility(View.GONE);
            }

            emailText.setText(customer.getEmail() != null && !customer.getEmail().isEmpty() ? customer.getEmail() : "—");
            phoneText.setText(customer.getPhone() != null && !customer.getPhone().isEmpty() ? customer.getPhone() : "—");
            cityText.setText(customer.getCity() != null && !customer.getCity().isEmpty() ? customer.getCity() : "—");

            if (Boolean.FALSE.equals(customer.getActive())) {
                itemView.setAlpha(0.6f);
            } else {
                itemView.setAlpha(1.0f);
            }

            editBtn.setOnClickListener(v -> listener.onEdit(customer));
            deleteBtn.setOnClickListener(v -> listener.onDelete(customer));
            historyBtn.setOnClickListener(v -> listener.onViewHistory(customer));
        }
    }
}
