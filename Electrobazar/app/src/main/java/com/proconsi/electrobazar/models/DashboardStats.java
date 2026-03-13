package com.proconsi.electrobazar.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class DashboardStats {
    @SerializedName("shiftActive")
    private boolean shiftActive;

    @SerializedName("shiftOpeningTime")
    private String shiftOpeningTime;

    @SerializedName("revenue")
    private BigDecimal revenue;

    @SerializedName("salesCount")
    private int salesCount;

    @SerializedName("topProduct")
    private String topProduct;

    @SerializedName("lowStockCount")
    private int lowStockCount;

    @SerializedName("openingBalance")
    private BigDecimal openingBalance;

    public boolean isShiftActive() { return shiftActive; }
    public String getShiftOpeningTime() { return shiftOpeningTime; }
    public BigDecimal getRevenue() { return revenue; }
    public int getSalesCount() { return salesCount; }
    public String getTopProduct() { return topProduct; }
    public int getLowStockCount() { return lowStockCount; }
    public BigDecimal getOpeningBalance() { return openingBalance; }
}
