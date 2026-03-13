package com.proconsi.electrobazar.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proconsi.electrobazar.models.ActivityLog;
import com.proconsi.electrobazar.models.DashboardStats;
import com.proconsi.electrobazar.models.Product;
import com.proconsi.electrobazar.models.Sale;
import com.proconsi.electrobazar.repositories.DashboardRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private final MutableLiveData<DashboardStats> statsData = new MutableLiveData<>();

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
    public LiveData<DashboardStats> getStatsData() { return statsData; }

    public void loadDashboard(String period) {
        isLoading.setValue(true);
        
        // 1. Fetch Synchronized Stats (KPIs and Shift info)
        repository.getDashboardStats(period, new DashboardRepository.DataCallback<DashboardStats>() {
            @Override
            public void onSuccess(DashboardStats stats) {
                statsData.setValue(stats);
                totalRevenue.setValue(stats.getRevenue());
                totalSalesCount.setValue(stats.getSalesCount());
                topProduct.setValue(stats.getTopProduct());
                lowStockCount.setValue(stats.getLowStockCount());
                
                // Now load chart data
                loadChartAndActivityData(period);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                // Try to load fallback chart data anyway
                loadChartAndActivityData(period);
            }
        });
    }

    private void loadChartAndActivityData(String period) {
        LocalDateTime fromDate;
        if (period == null || period.equals("shift") || period.equals("today")) {
            fromDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        } else if (period.equals("7days")) {
            fromDate = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
        } else if (period.equals("1month")) {
            fromDate = LocalDateTime.now().minusMonths(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        } else {
            fromDate = LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }

        String from = fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String to = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        repository.getSalesRange(from, to, new DashboardRepository.DataCallback<List<Sale>>() {
            @Override
            public void onSuccess(List<Sale> sales) {
                salesData.setValue(sales);
                
                repository.getProducts(new DashboardRepository.DataCallback<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> products) {
                        productsData.setValue(products);
                        
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
}
