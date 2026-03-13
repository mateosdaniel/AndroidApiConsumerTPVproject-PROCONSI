package com.proconsi.electrobazar.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
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
import com.proconsi.electrobazar.models.DashboardStats;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.Sale;
import com.proconsi.electrobazar.viewmodels.DashboardViewModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DashboardFragment extends Fragment {

    private DashboardViewModel viewModel;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvRevenue, tvSalesCount, tvTopProduct, tvLowStock;
    private TextView labelRevenue, labelSales, labelChart;
    private LineChart lineChart;
    private PieChart pieChart;
    private com.google.android.material.chip.ChipGroup chipGroupPeriod;
    private String currentPeriod = "shift";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvRevenue = view.findViewById(R.id.tvRevenue);
        tvSalesCount = view.findViewById(R.id.tvSalesCount);
        tvTopProduct = view.findViewById(R.id.tvTopProduct);
        tvLowStock = view.findViewById(R.id.tvLowStock);
        labelRevenue = view.findViewById(R.id.labelRevenue);
        labelSales = view.findViewById(R.id.labelSales);
        labelChart = view.findViewById(R.id.labelChart);
        lineChart = view.findViewById(R.id.lineChart);
        pieChart = view.findViewById(R.id.pieChart);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        chipGroupPeriod = view.findViewById(R.id.chipGroupPeriod);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        setupCharts();
        observeViewModel();
        setupPeriodSelection();

        swipeRefresh.setOnRefreshListener(() -> viewModel.loadDashboard(currentPeriod));

        if (savedInstanceState == null) {
            viewModel.loadDashboard(currentPeriod);
        }

        return view;
    }

    private void setupPeriodSelection() {
        chipGroupPeriod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipShift) {
                currentPeriod = "shift";
            } else if (checkedId == R.id.chipToday) {
                currentPeriod = "today";
            } else if (checkedId == R.id.chip7Days) {
                currentPeriod = "7days";
            } else if (checkedId == R.id.chip1Month) {
                currentPeriod = "1month";
            }
            viewModel.loadDashboard(currentPeriod);
        });
    }

    private void setupCharts() {
        // Line Chart setup
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextColor(Color.parseColor("#8892a4"));
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.parseColor("#8892a4"));
        xAxis.setDrawGridLines(false);
        
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#8892a4"));
        lineChart.getAxisRight().setEnabled(false);

        // Pie Chart setup
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.getLegend().setTextColor(Color.parseColor("#8892a4"));
        pieChart.setDrawEntryLabels(false);
    }

    private void observeViewModel() {
        viewModel.getTotalRevenue().observe(getViewLifecycleOwner(), revenue -> 
            tvRevenue.setText(String.format(Locale.getDefault(), "%.2f €", revenue)));

        viewModel.getTotalSalesCount().observe(getViewLifecycleOwner(), count -> 
            tvSalesCount.setText(String.valueOf(count)));

        viewModel.getTopProduct().observe(getViewLifecycleOwner(), product -> 
            tvTopProduct.setText(product));

        viewModel.getLowStockCount().observe(getViewLifecycleOwner(), count -> 
            tvLowStock.setText(String.valueOf(count)));

        viewModel.getStatsData().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                String periodLabel;
                switch (currentPeriod) {
                    case "shift":
                        periodLabel = stats.isShiftActive() ? "Turno" : "Hoy";
                        break;
                    case "today":
                        periodLabel = "Hoy";
                        break;
                    case "7days":
                        periodLabel = "7 Días";
                        break;
                    case "1month":
                        periodLabel = "Mes";
                        break;
                    default:
                        periodLabel = "Periodo";
                }

                if (labelRevenue != null) labelRevenue.setText("Ventas " + periodLabel);
                if (labelSales != null) labelSales.setText("Pedidos " + periodLabel);
                if (labelChart != null) {
                    if (currentPeriod.equals("shift") || currentPeriod.equals("today")) {
                        labelChart.setText("Tendencia por Horas (" + periodLabel + ")");
                    } else {
                        labelChart.setText("Tendencia Diaria (" + periodLabel + ")");
                    }
                }

                if (currentPeriod.equals("shift") && !stats.isShiftActive()) {
                    Toast.makeText(getContext(), "Caja cerrada. Mostrando ventas de hoy.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getSalesData().observe(getViewLifecycleOwner(), sales -> {
            DashboardStats stats = viewModel.getStatsData().getValue();
            if (currentPeriod.equals("shift") || currentPeriod.equals("today")) {
                updateHourlyChart(sales);
            } else {
                updateLineChart(sales);
            }
        });

        viewModel.getProductsData().observe(getViewLifecycleOwner(), this::updatePieChart);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> 
            swipeRefresh.setRefreshing(loading));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateHourlyChart(List<Sale> sales) {
        if (sales == null) return;
        
        Map<Integer, Float> hourlyStats = new TreeMap<>();
        for (int i = 0; i < 24; i++) hourlyStats.put(i, 0f);

        String todayStr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        for (Sale sale : sales) {
            if (sale.getCreatedAt().startsWith(todayStr)) {
                try {
                    LocalDateTime ldt = LocalDateTime.parse(sale.getCreatedAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    int hour = ldt.getHour();
                    hourlyStats.put(hour, hourlyStats.getOrDefault(hour, 0f) + (sale.getTotalAmount() != null ? sale.getTotalAmount().floatValue() : 0f));
                } catch (Exception ignored) {}
            }
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<Integer, Float> entry : hourlyStats.entrySet()) {
            entries.add(new Entry(index, entry.getValue()));
            labels.add(entry.getKey() + ":00");
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Ventas por hora (€)");
        dataSet.setColor(Color.parseColor("#f5a623"));
        dataSet.setCircleColor(Color.parseColor("#f5a623"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(30);
        dataSet.setFillColor(Color.parseColor("#f5a623"));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.invalidate();
    }

    private void updateLineChart(List<Sale> sales) {
        if (sales == null || sales.isEmpty()) return;

        Map<String, Float> dailyStats = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");

        int days = 7;
        if (currentPeriod.equals("1month")) days = 30;
        else if (currentPeriod.equals("today") || currentPeriod.equals("shift")) days = 1;

        // Initialize days with 0
        for (int i = days - 1; i >= 0; i--) {
            String dateLabel = LocalDateTime.now().minusDays(i).format(formatter);
            dailyStats.put(dateLabel, 0f);
        }

        for (Sale sale : sales) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(sale.getCreatedAt(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                String dateLabel = ldt.format(formatter);
                if (dailyStats.containsKey(dateLabel)) {
                    float current = dailyStats.get(dateLabel);
                    dailyStats.put(dateLabel, current + (sale.getTotalAmount() != null ? sale.getTotalAmount().floatValue() : 0f));
                }
            } catch (Exception ignored) {}
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Float> entry : dailyStats.entrySet()) {
            entries.add(new Entry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Ventas (€)");
        dataSet.setColor(Color.parseColor("#f5a623"));
        dataSet.setCircleColor(Color.parseColor("#f5a623"));
        dataSet.setLineWidth(2f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#f5a623"));
        dataSet.setFillAlpha(30);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.invalidate();
    }

    private void updatePieChart(List<Product> products) {
        if (products == null || products.isEmpty()) return;

        Map<String, Integer> catCounts = new HashMap<>();
        for (Product p : products) {
            String cat = (p.getCategory() != null) ? p.getCategory().getName() : "Sin Categoría";
            catCounts.put(cat, catCounts.getOrDefault(cat, 0) + 1);
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : catCounts.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        List<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
