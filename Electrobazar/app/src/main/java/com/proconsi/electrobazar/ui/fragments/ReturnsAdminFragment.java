package com.proconsi.electrobazar.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.models.SaleReturn;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.network.ReturnsRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnsAdminFragment extends Fragment {

    private static final String TAG = "ReturnsAdminFragment";

    private View root;
    private ReturnsRepository returnsRepository;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView returnsRecyclerView;
    private TextView emptyStateText;
    private TextView searchFilter;
    private com.google.android.material.chip.ChipGroup methodChips;
    private View btnDateRange;

    private List<SaleReturn> allReturns = new ArrayList<>();
    private ReturnsAdapter adapter;

    private Long dateFrom, dateTo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_returns_admin, container, false);

        returnsRepository = new ReturnsRepository(com.proconsi.electrobazar.network.RetrofitClient.getInstance().getApi());

        initViews();
        loadReturns();


        return root;
    }

    private void initViews() {
        swipeRefresh = root.findViewById(R.id.swipeRefresh);
        returnsRecyclerView = root.findViewById(R.id.returnsRecyclerView);
        emptyStateText = root.findViewById(R.id.emptyStateText);
        searchFilter = root.findViewById(R.id.searchFilter);
        methodChips = root.findViewById(R.id.methodChips);
        btnDateRange = root.findViewById(R.id.btnDateRange);

        returnsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReturnsAdapter(new ArrayList<>(), this::downloadPdf);
        returnsRecyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadReturns);

        searchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        methodChips.setOnCheckedChangeListener((group, checkedId) -> applyFilters());

        btnDateRange.setOnClickListener(v -> showDateRangePicker());
    }

    private void showDateRangePicker() {
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Seleccionar Rango")
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            dateFrom = selection.first;
            dateTo = selection.second;
            loadReturns();
        });

        picker.show(getChildFragmentManager(), "date_picker");
    }

    private void loadReturns() {
        swipeRefresh.setRefreshing(true);

        String fromStr = null;
        String toStr = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        if (dateFrom != null) fromStr = sdf.format(new Date(dateFrom));
        if (dateTo != null) toStr = sdf.format(new Date(dateTo + 86399000)); // End of day

        returnsRepository.getReturns(fromStr, toStr).enqueue(new Callback<List<SaleReturn>>() {
            @Override
            public void onResponse(Call<List<SaleReturn>> call, Response<List<SaleReturn>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    allReturns = response.body();
                    applyFilters();
                } else {
                    Toast.makeText(getContext(), "Error al cargar devoluciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SaleReturn>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String query = searchFilter.getText().toString().toLowerCase().trim();
        int checkedId = methodChips.getCheckedChipId();
        String methodFilter = null;
        if (checkedId == R.id.chipCash) methodFilter = "CASH";
        else if (checkedId == R.id.chipCard) methodFilter = "CARD";

        String finalMethodFilter = methodFilter;
        List<SaleReturn> filtered = allReturns.stream().filter(r -> {
            boolean matchesQuery = query.isEmpty() || 
                    r.getReturnNumber().toLowerCase().contains(query);
            
            boolean matchesMethod = finalMethodFilter == null || 
                    (r.getPaymentMethod() != null && r.getPaymentMethod().name().equals(finalMethodFilter));
            
            return matchesQuery && matchesMethod;
        }).collect(Collectors.toList());

        adapter.updateData(filtered);
        emptyStateText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void downloadPdf(SaleReturn saleReturn) {
        returnsRepository.downloadReturnPdf(saleReturn.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveAndOpenPdf(response.body(), "Devolucion_" + saleReturn.getReturnNumber() + ".pdf");
                } else {
                    Toast.makeText(getContext(), "Error al descargar PDF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAndOpenPdf(ResponseBody body, String filename) {
        try {
            File path = new File(getContext().getExternalFilesDir(null), "returns");
            if (!path.exists()) path.mkdirs();
            File file = new File(path, filename);

            try (InputStream inputStream = body.byteStream();
                 OutputStream outputStream = new FileOutputStream(file)) {
                byte[] data = new byte[4096];
                int read;
                while ((read = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, read);
                }
                outputStream.flush();
            }

            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (IOException e) {
            Log.e(TAG, "Error saving PDF", e);
            Toast.makeText(getContext(), "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Adapter ---

    private interface ReturnActionListener {
        void onDownloadPdf(SaleReturn saleReturn);
    }

    private static class ReturnsAdapter extends RecyclerView.Adapter<ReturnsAdapter.ViewHolder> {
        private List<SaleReturn> items;
        private final ReturnActionListener listener;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        ReturnsAdapter(List<SaleReturn> items, ReturnActionListener listener) {
            this.items = items;
            this.listener = listener;
        }

        void updateData(List<SaleReturn> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_return_admin, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SaleReturn r = items.get(position);
            holder.returnNumber.setText(r.getReturnNumber());
            
            String dateStr = r.getCreatedAt();
            if (dateStr != null && dateStr.contains("T")) {
                try {
                    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date date = parser.parse(dateStr);
                    if (date != null) dateStr = dateFormat.format(date);
                } catch (Exception ignored) {}
            }
            holder.returnDate.setText(dateStr);

            holder.returnSaleRef.setText("Ticket Ref: N/A");
            holder.returnWorker.setText("Atendido por: " + (r.getWorker() != null ? r.getWorker().getUsername() : "Sistema"));
            holder.refundMethod.setText(r.getPaymentMethod() != null ? r.getPaymentMethod().name() : "N/A");
            holder.refundAmount.setText(String.format("%.2f€", r.getTotalRefunded()));

            holder.btnDownloadPdf.setOnClickListener(v -> listener.onDownloadPdf(r));
        }


        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView returnNumber, returnDate, returnSaleRef, returnWorker, refundMethod, refundAmount;
            View btnDownloadPdf;

            ViewHolder(View itemView) {
                super(itemView);
                returnNumber = itemView.findViewById(R.id.returnNumber);
                returnDate = itemView.findViewById(R.id.returnDate);
                returnSaleRef = itemView.findViewById(R.id.returnSaleRef);
                returnWorker = itemView.findViewById(R.id.returnWorker);
                refundMethod = itemView.findViewById(R.id.refundMethod);
                refundAmount = itemView.findViewById(R.id.refundAmount);
                btnDownloadPdf = itemView.findViewById(R.id.btnDownloadPdf);
            }
        }
    }
}
