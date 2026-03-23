package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.SuspendedSaleResponse;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.ui.adapters.SuspendedSalesAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuspendedSalesDialogFragment extends BottomSheetDialogFragment implements SuspendedSalesAdapter.OnSuspendedSaleActionListener {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView emptyState;
    private SuspendedSalesAdapter adapter;
    private ApiService apiService;
    private OnSuspendedSaleSelectedListener listener;

    public interface OnSuspendedSaleSelectedListener {
        void onSaleSelected(SuspendedSaleResponse sale);
    }

    public void setListener(OnSuspendedSaleSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_suspended_sales, container, false);
        
        swipeRefresh = view.findViewById(R.id.swipeRefreshSuspended);
        recyclerView = view.findViewById(R.id.rvSuspendedSales);
        emptyState = view.findViewById(R.id.tvEmptySuspended);
        
        apiService = RetrofitClient.getInstance().getApi();
        adapter = new SuspendedSalesAdapter(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        swipeRefresh.setOnRefreshListener(this::loadSuspendedSales);
        
        loadSuspendedSales();
        
        return view;
    }

    private void loadSuspendedSales() {
        swipeRefresh.setRefreshing(true);
        apiService.getSuspendedSales().enqueue(new Callback<List<SuspendedSaleResponse>>() {
            @Override
            public void onResponse(Call<List<SuspendedSaleResponse>> call, Response<List<SuspendedSaleResponse>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<SuspendedSaleResponse> sales = response.body();
                    adapter.setSales(sales);
                    emptyState.setVisibility(sales.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), "Error al cargar ventas en espera", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SuspendedSaleResponse>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume(SuspendedSaleResponse sale) {
        if (listener != null) {
            listener.onSaleSelected(sale);
        }
        dismiss();
    }

    @Override
    public void onCancel(SuspendedSaleResponse sale) {
        apiService.cancelSuspendedSale(sale.getId()).enqueue(new Callback<SuspendedSaleResponse>() {
            @Override
            public void onResponse(Call<SuspendedSaleResponse> call, Response<SuspendedSaleResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Venta cancelada", Toast.LENGTH_SHORT).show();
                    loadSuspendedSales();
                } else {
                    Toast.makeText(getContext(), "Error al cancelar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SuspendedSaleResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
