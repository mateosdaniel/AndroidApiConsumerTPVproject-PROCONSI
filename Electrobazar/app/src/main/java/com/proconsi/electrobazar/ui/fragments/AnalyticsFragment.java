package com.proconsi.electrobazar.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.databinding.FragmentAnalyticsBinding;
import com.proconsi.electrobazar.models.DashboardStats;
import com.proconsi.electrobazar.models.PagedResponse;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.Sale;
import com.proconsi.electrobazar.network.RetrofitClient;
import com.proconsi.electrobazar.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;
    private ApiService apiService;
    private String currentPeriod = "7days";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        com.proconsi.electrobazar.utils.ThemeManager.applyFontToView(binding.getRoot(), requireContext());
        apiService = RetrofitClient.getInstance().getApi();

        setupPeriodSpinner();
        setupSwipeRefresh();
        
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        
        loadData();

        return binding.getRoot();
    }

    private void setupPeriodSpinner() {
        String[] periods = {"Hoy", "Últimos 7 días", "Mensual", "Anual", "Todo"};
        final String[] periodValues = {"today", "7days", "1month", "1year", "all"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, periods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPeriod.setAdapter(adapter);
        binding.spinnerPeriod.setSelection(1); // Default to 7 days

        binding.spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPeriod = periodValues[position];
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshAnalytics.setOnRefreshListener(this::loadData);
        binding.swipeRefreshAnalytics.setColorSchemeColors(getResources().getColor(R.color.accent));
    }

    private void loadData() {
        binding.swipeRefreshAnalytics.setRefreshing(true);

        // Load 4 main stats
        apiService.getDashboardStats(currentPeriod).enqueue(new Callback<DashboardStats>() {
            @Override
            public void onResponse(Call<DashboardStats> call, Response<DashboardStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateStatsUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<DashboardStats> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar estadísticas", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Sales for LineChart
        fetchSalesAndProducts();
    }

    private void updateStatsUI(DashboardStats stats) {
        binding.tvStatRevenue.setText(String.format(Locale.getDefault(), "%.2f €", stats.getRevenue()));
        binding.tvStatSalesCount.setText(String.valueOf(stats.getSalesCount()));
        binding.tvStatTopProduct.setText(stats.getTopProduct());
        binding.tvStatLowStock.setText(String.valueOf(stats.getLowStockCount()));
    }

    private void fetchSalesAndProducts() {
        String fromDate;
        String toDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Calendar cal = Calendar.getInstance();
        if (currentPeriod.equals("today")) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else if (currentPeriod.equals("7days")) {
            cal.add(Calendar.DAY_OF_YEAR, -7);
        } else if (currentPeriod.equals("1month")) {
            cal.add(Calendar.MONTH, -1);
        } else if (currentPeriod.equals("1year")) {
            cal.add(Calendar.YEAR, -1);
        } else {
            cal.set(2020, 0, 1);
        }
        fromDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(cal.getTime());

        apiService.getSalesRange(fromDate, toDate).enqueue(new Callback<PagedResponse<Sale>>() {
            @Override
            public void onResponse(Call<PagedResponse<Sale>> call, Response<PagedResponse<Sale>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateLineChart(response.body().getContent());
                }
                checkRefreshDone();
            }

            @Override
            public void onFailure(Call<PagedResponse<Sale>> call, Throwable t) {
                checkRefreshDone();
            }
        });

        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updatePieChart(response.body());
                }
                checkRefreshDone();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                checkRefreshDone();
            }
        });
    }

    private int pendingRequests = 2;
    private void checkRefreshDone() {
        pendingRequests--;
        if (pendingRequests <= 0) {
            binding.swipeRefreshAnalytics.setRefreshing(false);
            pendingRequests = 2;
        }
    }

    private void updateLineChart(List<Sale> sales) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Group sales by day (simplified)
        Map<String, Float> dayTotals = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        
        Calendar cal = Calendar.getInstance();
        int days = currentPeriod.equals("today") ? 24 : (currentPeriod.equals("7days") ? 7 : 30);
        
        if (currentPeriod.equals("today")) {
            // Hours
            for (int i = 0; i < 24; i++) {
                String key = String.format(Locale.getDefault(), "%02d:00", i);
                dayTotals.put(key, 0f);
                labels.add(key);
            }
            for (Sale sale : sales) {
                try {
                    // Assuming sale.getCreatedAt() is ISO format
                    int hour = Integer.parseInt(sale.getCreatedAt().substring(11, 13));
                    String key = String.format(Locale.getDefault(), "%02d:00", hour);
                    dayTotals.put(key, dayTotals.getOrDefault(key, 0f) + sale.getTotalAmount().floatValue());
                } catch (Exception ignored) {}
            }
        } else {
            // Days
            for (int i = days - 1; i >= 0; i--) {
                Calendar d = Calendar.getInstance();
                d.add(Calendar.DAY_OF_YEAR, -i);
                String key = sdf.format(d.getTime());
                dayTotals.put(key, 0f);
                labels.add(key);
            }
            for (Sale sale : sales) {
                try {
                    String sDateStr = sale.getCreatedAt().substring(0, 10); // yyyy-MM-dd
                    SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String key = sdf.format(iso.parse(sDateStr));
                    if (dayTotals.containsKey(key)) {
                        dayTotals.put(key, dayTotals.get(key) + sale.getTotalAmount().floatValue());
                    }
                } catch (Exception ignored) {}
            }
        }

        for (int i = 0; i < labels.size(); i++) {
            entries.add(new Entry(i, dayTotals.get(labels.get(i))));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Ventas (€)");
        dataSet.setColor(Color.parseColor("#f5a623"));
        dataSet.setCircleColor(Color.parseColor("#f5a623"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#f5a623"));
        dataSet.setFillAlpha(30);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        binding.salesLineChart.setData(lineData);
        binding.salesLineChart.getDescription().setEnabled(false);
        binding.salesLineChart.getLegend().setTextColor(Color.parseColor("#8892a4"));
        
        XAxis xAxis = binding.salesLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.parseColor("#8892a4"));
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        binding.salesLineChart.getAxisLeft().setTextColor(Color.parseColor("#8892a4"));
        binding.salesLineChart.getAxisLeft().setDrawGridLines(true);
        binding.salesLineChart.getAxisLeft().setGridColor(Color.parseColor("#22ffffff"));
        binding.salesLineChart.getAxisRight().setEnabled(false);

        binding.salesLineChart.animateX(800);
        binding.salesLineChart.invalidate();
    }

    private void updatePieChart(List<Product> products) {
        Map<String, Integer> catCount = new HashMap<>();
        for (Product p : products) {
            String cat = p.getCategory() != null ? p.getCategory().getName() : "Sin Categoría";
            catCount.put(cat, catCount.getOrDefault(cat, 0) + 1);
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : catCount.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(3f);

        PieData pieData = new PieData(dataSet);
        binding.categoryPieChart.setData(pieData);
        binding.categoryPieChart.getDescription().setEnabled(false);
        binding.categoryPieChart.getLegend().setTextColor(Color.parseColor("#8892a4"));
        binding.categoryPieChart.getLegend().setOrientation(com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL);
        binding.categoryPieChart.getLegend().setTextSize(14f);
        binding.categoryPieChart.getLegend().setFormSize(14f);
        binding.categoryPieChart.setDrawEntryLabels(false); // Hide category names on slices
        binding.categoryPieChart.setHoleColor(Color.TRANSPARENT);
        binding.categoryPieChart.setCenterText("Productos");
        binding.categoryPieChart.setCenterTextColor(Color.WHITE);
        binding.categoryPieChart.setEntryLabelColor(Color.WHITE);

        binding.categoryPieChart.animateY(800);
        binding.categoryPieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
