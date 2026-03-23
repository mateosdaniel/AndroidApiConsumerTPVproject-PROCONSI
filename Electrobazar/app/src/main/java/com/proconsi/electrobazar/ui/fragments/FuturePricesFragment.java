package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.databinding.FragmentFuturePricesBinding;
import com.proconsi.electrobazar.models.ProductPriceResponse;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.ui.adapters.PriceHistoryAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FuturePricesFragment extends Fragment {

    private FragmentFuturePricesBinding binding;
    private PriceHistoryAdapter adapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFuturePricesBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getInstance().getApi();
        
        setupRecyclerView();
        
        binding.swipeRefresh.setOnRefreshListener(this::loadFuturePrices);
        loadFuturePrices();
        
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new PriceHistoryAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadFuturePrices() {
        binding.swipeRefresh.setRefreshing(true);
        apiService.getFuturePrices().enqueue(new Callback<List<ProductPriceResponse>>() {
            @Override
            public void onResponse(Call<List<ProductPriceResponse>> call, Response<List<ProductPriceResponse>> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductPriceResponse> prices = response.body();
                    adapter.setItems(prices);
                    binding.tvEmptyState.setVisibility(prices.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), "Error al cargar precios programados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductPriceResponse>> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
