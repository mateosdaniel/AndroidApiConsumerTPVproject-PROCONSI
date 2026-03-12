package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class SaleSummaryResponse {
    private long totalSalesCount;
    private BigDecimal totalSalesAmount;
    private BigDecimal totalCashAmount;
    private BigDecimal totalCardAmount;
    private long totalCancelledCount;
    private BigDecimal totalCancelledAmount;

    public long getTotalSalesCount() { return totalSalesCount; }
    public void setTotalSalesCount(long totalSalesCount) { this.totalSalesCount = totalSalesCount; }

    public BigDecimal getTotalSalesAmount() { return totalSalesAmount; }
    public void setTotalSalesAmount(BigDecimal totalSalesAmount) { this.totalSalesAmount = totalSalesAmount; }

    public BigDecimal getTotalCashAmount() { return totalCashAmount; }
    public void setTotalCashAmount(BigDecimal totalCashAmount) { this.totalCashAmount = totalCashAmount; }

    public BigDecimal getTotalCardAmount() { return totalCardAmount; }
    public void setTotalCardAmount(BigDecimal totalCardAmount) { this.totalCardAmount = totalCardAmount; }

    public long getTotalCancelledCount() { return totalCancelledCount; }
    public void setTotalCancelledCount(long totalCancelledCount) { this.totalCancelledCount = totalCancelledCount; }

    public BigDecimal getTotalCancelledAmount() { return totalCancelledAmount; }
    public void setTotalCancelledAmount(BigDecimal totalCancelledAmount) { this.totalCancelledAmount = totalCancelledAmount; }
}
