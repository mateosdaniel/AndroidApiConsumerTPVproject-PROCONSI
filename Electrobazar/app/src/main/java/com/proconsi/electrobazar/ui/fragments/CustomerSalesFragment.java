package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.ui.adapters.CustomerSalesAdapter;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerSalesFragment extends Fragment {

    private static final String ARG_CUSTOMER_ID = "customer_id";
    private static final String ARG_CUSTOMER_NAME = "customer_name";

    private Long customerId;
    private String customerName;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private CustomerSalesAdapter adapter;
    private TextView summaryText;

    public static CustomerSalesFragment newInstance(Long id, String name) {
        CustomerSalesFragment fragment = new CustomerSalesFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CUSTOMER_ID, id);
        args.putString(ARG_CUSTOMER_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = getArguments().getLong(ARG_CUSTOMER_ID);
            customerName = getArguments().getString(ARG_CUSTOMER_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_sales, container, false);

        Toolbar toolbar = view.findViewById(R.id.historyToolbar);
        toolbar.setTitle("Compras: " + customerName);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        summaryText = view.findViewById(R.id.customerSummaryText);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.salesRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CustomerSalesAdapter(this::onSaleClick);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadHistory);

        loadHistory();

        return view;
    }

    private void loadHistory() {
        swipeRefresh.setRefreshing(true);
        RetrofitClient.getInstance().getApi().getCustomerSales(customerId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> sales = response.body();
                    adapter.setSales(sales);
                    summaryText.setText(String.format("Mostrando %d ventas realizadas por %s", sales.size(), customerName));
                } else {
                    Toast.makeText(getContext(), "No se pudo cargar el historial", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onSaleClick(Map<String, Object> sale) {
        // Show detail or something?
        Toast.makeText(getContext(), "Venta #" + sale.get("id"), Toast.LENGTH_SHORT).show();
    }
}
