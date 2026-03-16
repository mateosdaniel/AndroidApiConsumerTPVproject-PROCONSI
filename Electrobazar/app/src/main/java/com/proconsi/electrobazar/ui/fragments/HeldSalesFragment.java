package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.SuspendedSaleResponse;
import com.proconsi.electrobazar.repositories.HeldSalesRepository;
import com.proconsi.electrobazar.ui.adapters.HeldSalesAdapter;
import com.proconsi.electrobazar.viewmodels.SaleViewModel;

import java.util.List;

public class HeldSalesFragment extends Fragment {

    private HeldSalesRepository heldSalesRepository;
    private SaleViewModel saleViewModel;
    private HeldSalesAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private View emptyState;
    private ProgressBar loadingProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_held_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        heldSalesRepository = new HeldSalesRepository();
        saleViewModel = new ViewModelProvider(requireActivity()).get(SaleViewModel.class);

        initViews(view);
        loadHeldSales();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.heldSalesRecyclerView);
        emptyState = view.findViewById(R.id.emptyState);
        loadingProgress = view.findViewById(R.id.loadingProgress);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HeldSalesAdapter(new HeldSalesAdapter.OnHeldSaleInteractionListener() {
            @Override
            public void onRecover(SuspendedSaleResponse heldSale) {
                recoverHeldSale(heldSale);
            }

            @Override
            public void onDelete(SuspendedSaleResponse heldSale) {
                confirmDelete(heldSale);
            }
        });
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadHeldSales);
        swipeRefresh.setColorSchemeResources(R.color.accent);
    }

    private void loadHeldSales() {
        loadingProgress.setVisibility(View.VISIBLE);
        heldSalesRepository.getHeldSales().observe(getViewLifecycleOwner(), sales -> {
            loadingProgress.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            
            if (sales != null) {
                adapter.setHeldSales(sales);
                emptyState.setVisibility(sales.isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                Toast.makeText(getContext(), "Error al cargar ventas en espera", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void recoverHeldSale(SuspendedSaleResponse heldSale) {
        loadingProgress.setVisibility(View.VISIBLE);
        heldSalesRepository.recoverSale(heldSale.getId()).observe(getViewLifecycleOwner(), response -> {
            loadingProgress.setVisibility(View.GONE);
            if (response != null) {
                saleViewModel.loadHeldSale(response);
                Toast.makeText(getContext(), "Venta recuperada", Toast.LENGTH_SHORT).show();
                // Navigate back to SaleFragment
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(getContext(), "Error al recuperar la venta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(SuspendedSaleResponse heldSale) {
        new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Eliminar venta en espera")
                .setMessage("¿Estás seguro de que deseas eliminar esta venta en espera? Esta acción no se puede deshacer.")
                .setPositiveButton("ELIMINAR", (dialog, which) -> deleteHeldSale(heldSale))
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void deleteHeldSale(SuspendedSaleResponse heldSale) {
        loadingProgress.setVisibility(View.VISIBLE);
        heldSalesRepository.deleteHeldSale(heldSale.getId()).observe(getViewLifecycleOwner(), success -> {
            loadingProgress.setVisibility(View.GONE);
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), "Venta eliminada", Toast.LENGTH_SHORT).show();
                loadHeldSales();
            } else {
                Toast.makeText(getContext(), "Error al eliminar la venta", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
