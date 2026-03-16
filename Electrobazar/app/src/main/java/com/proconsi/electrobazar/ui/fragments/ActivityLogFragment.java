package com.proconsi.electrobazar.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proconsi.electrobazar.databinding.FragmentActivityLogBinding;
import com.proconsi.electrobazar.models.ActivityLog;
import com.proconsi.electrobazar.network.ApiClient;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.ui.adapters.ActivityLogAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogFragment extends Fragment {

    private FragmentActivityLogBinding binding;
    private ActivityLogAdapter adapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActivityLogBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient().create(ApiService.class);

        setupRecyclerView();
        setupFilters();
        setupSwipeRefresh();

        loadLogs();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new ActivityLogAdapter();
        binding.rvActivityLog.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvActivityLog.setAdapter(adapter);
    }

    private void setupFilters() {
        TextWatcher filterWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etSearchUser.addTextChangedListener(filterWatcher);
        binding.etSearchAction.addTextChangedListener(filterWatcher);
    }

    private void applyFilters() {
        String user = binding.etSearchUser.getText().toString();
        String action = binding.etSearchAction.getText().toString();
        adapter.filter(user, action);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshActivity.setOnRefreshListener(this::loadLogs);
        binding.swipeRefreshActivity.setColorSchemeColors(getResources().getColor(com.proconsi.electrobazar.R.color.accent));
    }

    private void loadLogs() {
        binding.swipeRefreshActivity.setRefreshing(true);
        apiService.getRecentActivityLogs().enqueue(new Callback<List<ActivityLog>>() {
            @Override
            public void onResponse(Call<List<ActivityLog>> call, Response<List<ActivityLog>> response) {
                binding.swipeRefreshActivity.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setLogs(response.body());
                    applyFilters();
                } else {
                    Toast.makeText(getContext(), "Error al cargar logs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ActivityLog>> call, Throwable t) {
                binding.swipeRefreshActivity.setRefreshing(false);
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
