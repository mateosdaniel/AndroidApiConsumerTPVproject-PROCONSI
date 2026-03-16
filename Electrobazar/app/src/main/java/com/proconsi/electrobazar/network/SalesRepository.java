package com.proconsi.electrobazar.network;

import com.proconsi.electrobazar.models.Sale;
import com.proconsi.electrobazar.models.SaleSummaryResponse;

import java.util.List;

import retrofit2.Call;

public class SalesRepository {
    private final ApiService apiService;

    public SalesRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<List<Sale>> getSales() {
        return apiService.getSales();
    }

    public Call<Sale> getSaleById(Long id) {
        return apiService.getSaleById(id);
    }

    public Call<List<Sale>> getTodaySales() {
        return apiService.getTodaySales();
    }

    public Call<SaleSummaryResponse> getTodaySalesStats() {
        return apiService.getTodaySalesStats();
    }

    public Call<List<Sale>> getSalesRange(String from, String to) {
        return apiService.getSalesRange(from, to);
    }
}
