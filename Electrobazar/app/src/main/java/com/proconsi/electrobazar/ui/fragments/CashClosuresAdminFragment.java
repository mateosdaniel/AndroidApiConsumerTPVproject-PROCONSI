package com.proconsi.electrobazar.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.databinding.FragmentCashClosuresAdminBinding;
import com.proconsi.electrobazar.models.CashRegister;
import com.proconsi.electrobazar.models.Worker;
import com.proconsi.electrobazar.network.ApiService;
import com.proconsi.electrobazar.network.CashRegisterRepository;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.ui.adapters.CashClosureAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashClosuresAdminFragment extends Fragment implements CashClosureAdapter.OnPdfDownloadListener {

    private FragmentCashClosuresAdminBinding binding;
    private CashRegisterRepository repository;
    private CashClosureAdapter adapter;
    private List<CashRegister> closures = new ArrayList<>();
    private List<Worker> workers = new ArrayList<>();
    private Long selectedWorkerId = null;
    private Long startDate = null;
    private Long endDate = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCashClosuresAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new CashRegisterRepository(RetrofitClient.getInstance().getApi());

        setupRecyclerView();
        setupFilters();
        loadWorkers();
        loadClosures();

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new CashClosureAdapter(closures, this);
        binding.closuresRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.closuresRecyclerView.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadClosures);
    }

    private void setupFilters() {
        binding.btnDateRange.setOnClickListener(v -> {
            MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Seleccionar periodo")
                    .build();
            picker.show(getChildFragmentManager(), "DATE_PICKER");
            picker.addOnPositiveButtonClickListener(selection -> {
                startDate = selection.first;
                endDate = selection.second;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                binding.btnDateRange.setText(sdf.format(new Date(startDate)) + " - " + sdf.format(new Date(endDate)));
                loadClosures();
            });
        });

        binding.workerFilter.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                selectedWorkerId = null;
            } else {
                selectedWorkerId = workers.get(position - 1).getId();
            }
            loadClosures();
        });
    }

    private void loadWorkers() {
        RetrofitClient.getInstance().getApi().getAllWorkers().enqueue(new Callback<List<Worker>>() {
            @Override
            public void onResponse(Call<List<Worker>> call, Response<List<Worker>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    workers = response.body();
                    List<String> names = new ArrayList<>();
                    names.add("Todos los trabajadores");
                    for (Worker w : workers) {
                        names.add(w.getUsername());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names);
                    binding.workerFilter.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Worker>> call, Throwable t) {
            }
        });
    }

    private void loadClosures() {
        binding.swipeRefresh.setRefreshing(true);
        repository.getAllClosedRegisters().enqueue(new Callback<List<CashRegister>>() {
            @Override
            public void onResponse(Call<List<CashRegister>> call, Response<List<CashRegister>> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    closures.clear();
                    List<CashRegister> filtered = filterLocally(response.body());
                    closures.addAll(filtered);
                    adapter.notifyDataSetChanged();
                    binding.emptyStateText.setVisibility(closures.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<CashRegister>> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(requireContext(), "Error al cargar cierres", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<CashRegister> filterLocally(List<CashRegister> list) {
        List<CashRegister> filtered = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (CashRegister cr : list) {
            boolean workerMatch = selectedWorkerId == null || (cr.getWorker() != null && cr.getWorker().getId().equals(selectedWorkerId));
            
            boolean dateMatch = true;
            if (startDate != null && endDate != null) {
                try {
                    // Assuming registerDate is yyyy-MM-dd
                    Date regDate = sdf.parse(cr.getRegisterDate());
                    long regTime = regDate.getTime();
                    
                    // Normalize range to full days
                    Calendar startCal = Calendar.getInstance();
                    startCal.setTimeInMillis(startDate);
                    startCal.set(Calendar.HOUR_OF_DAY, 0);
                    startCal.set(Calendar.MINUTE, 0);
                    startCal.set(Calendar.SECOND, 0);
                    
                    Calendar endCal = Calendar.getInstance();
                    endCal.setTimeInMillis(endDate);
                    endCal.set(Calendar.HOUR_OF_DAY, 23);
                    endCal.set(Calendar.MINUTE, 59);
                    endCal.set(Calendar.SECOND, 59);
                    
                    dateMatch = (regTime >= startCal.getTimeInMillis() && regTime <= endCal.getTimeInMillis());
                } catch (Exception e) {
                    Log.e("CashClosures", "Error parsing date: " + cr.getRegisterDate(), e);
                }
            }
            
            if (workerMatch && dateMatch) {
                filtered.add(cr);
            }
        }
        return filtered;
    }

    @Override
    public void onDownload(CashRegister closure) {
        repository.downloadCashRegisterTicket(closure.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.proconsi.electrobazar.utils.PdfUtils.saveAndOpenFile(requireContext(), response.body(), "Cierre_Caja_" + closure.getId() + ".pdf");
                } else {
                    Toast.makeText(requireContext(), "Error al descargar PDF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
