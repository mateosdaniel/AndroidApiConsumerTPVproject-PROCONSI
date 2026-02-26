package com.example.electrobazar.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardData {

    @SerializedName("todaySalesCount")
    private int todaySalesCount;

    @SerializedName("todayTotalRevenue")
    private double todayTotalRevenue;

    @SerializedName("recentSales")
    private List<Sale> recentSales;

    @SerializedName("totalProducts")
    private int totalProducts;

    @SerializedName("totalWorkers")
    private int totalWorkers;

    @SerializedName("totalCustomers")
    private int totalCustomers;

    public int getTodaySalesCount() {
        return todaySalesCount;
    }

    public void setTodaySalesCount(int todaySalesCount) {
        this.todaySalesCount = todaySalesCount;
    }

    public double getTodayTotalRevenue() {
        return todayTotalRevenue;
    }

    public void setTodayTotalRevenue(double todayTotalRevenue) {
        this.todayTotalRevenue = todayTotalRevenue;
    }

    public List<Sale> getRecentSales() {
        return recentSales;
    }

    public void setRecentSales(List<Sale> recentSales) {
        this.recentSales = recentSales;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalWorkers() {
        return totalWorkers;
    }

    public void setTotalWorkers(int totalWorkers) {
        this.totalWorkers = totalWorkers;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
}
