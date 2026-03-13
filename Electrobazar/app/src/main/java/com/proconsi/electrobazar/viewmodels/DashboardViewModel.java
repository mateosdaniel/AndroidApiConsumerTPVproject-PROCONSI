package com.proconsi.electrobazar.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proconsi.electrobazar.models.ActivityLog;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.Sale;
import com.proconsi.electrobazar.models.SaleLine;
import com.proconsi.electrobazar.repositories.DashboardRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardViewModel extends ViewModel {

    private final DashboardRepository repository;
    private final MutableLiveData<BigDecimal> totalRevenue = new MutableLiveData<>(BigDecimal.ZERO);
    private final MutableLiveData<Integer> totalSalesCount = new MutableLiveData<>(0);
    private final MutableLiveData<String> topProduct = new MutableLiveData<>("—");
    private final MutableLiveData<Integer> lowStockCount = new MutableLiveData<>(0);
    private final MutableLiveData<List<Sale>> salesData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> productsData = new MutableLiveData<>();
    private final MutableLiveData<List<ActivityLog>> activityLogs = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DashboardViewModel() {
        this.repository = new DashboardRepository();
    }

    public LiveData<BigDecimal> getTotalRevenue() { return totalRevenue; }
    public LiveData<Integer> getTotalSalesCount() { return totalSalesCount; }
    public LiveData<String> getTopProduct() { return topProduct; }
    public LiveData<Integer> getLowStockCount() { return lowStockCount; }
    public LiveData<List<Sale>> getSalesData() { return salesData; }
    public LiveData<List<Product>> getProductsData() { return productsData; }
    public LiveData<List<ActivityLog>> getActivityLogs() { return activityLogs; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadDashboard() {
        isLoading.setValue(true);
        
        // Use today range for KPIs as per requirement
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        // Actually, for trend charts, the web uses 7 days by default.
        // But the requirement says "KPIs matching web dashboard" and then "Trend chart for sales trend".
        // Let's pull 7 days for the trend chart and filter today for KPIs.
        
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0);
        String from = sevenDaysAgo.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String to = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        repository.getSalesRange(from, to, new DashboardRepository.DataCallback<List<Sale>>() {
            @Override
            public void onSuccess(List<Sale> sales) {
                salesData.setValue(sales);
                calculateSalesStats(sales);
                
                repository.getProducts(new DashboardRepository.DataCallback<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> products) {
                        productsData.setValue(products);
                        calculateProductStats(products);
                        
                        repository.getRecentActivity(new DashboardRepository.DataCallback<List<ActivityLog>>() {
                            @Override
                            public void onSuccess(List<ActivityLog> logs) {
                                activityLogs.setValue(logs);
                                isLoading.setValue(false);
                            }

                            @Override
                            public void onError(String error) {
                                errorMessage.setValue(error);
                                isLoading.setValue(false);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        errorMessage.setValue(error);
                        isLoading.setValue(false);
                    }
                });
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    private void calculateSalesStats(List<Sale> sales) {
        BigDecimal revenue = BigDecimal.ZERO;
        int count = 0;
        Map<String, Integer> productQuantities = new HashMap<>();
        
        String todayStr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        for (Sale sale : sales) {
            // Check if sale is from today for the KPI cards
            String saleDateStr = sale.getCreatedAt().substring(0, 10);
            if (saleDateStr.equals(todayStr)) {
                revenue = revenue.add(sale.getTotalAmount() != null ? sale.getTotalAmount() : BigDecimal.ZERO);
                count++;
            }

            // Top product calculation usually includes the whole range loaded or just today?
            // Web dashboard uses the currently loaded sales range for top product.
            if (sale.getLines() != null) {
                for (SaleLine line : sale.getLines()) {
                    String pName = (line.getProduct() != null) ? line.getProduct().getName() : "Producto";
                    int qty = (line.getQuantity() != null) ? line.getQuantity() : 0;
                    productQuantities.put(pName, productQuantities.getOrDefault(pName, 0) + qty);
                }
            }
        }

        totalRevenue.setValue(revenue);
        totalSalesCount.setValue(count);

        String topP = "—";
        int maxQty = 0;
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            if (entry.getValue() > maxQty) {
                maxQty = entry.getValue();
                topP = entry.getKey();
            }
        }
        topProduct.setValue(topP);
    }

    private void calculateProductStats(List<Product> products) {
        int lowStock = 0;
        for (Product p : products) {
            if (p.getStock() != null && p.getStock() < 5) {
                lowStock++;
            }
        }
        lowStockCount.setValue(lowStock);
    }
}
