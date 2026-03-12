package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class TaxBreakdown {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal baseAmount;
    private BigDecimal vatRate;
    private BigDecimal vatAmount;
    private BigDecimal recargoRate;
    private BigDecimal recargoAmount;
    private BigDecimal totalAmount;
    private boolean recargoApplied;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }

    public BigDecimal getVatAmount() { return vatAmount; }
    public void setVatAmount(BigDecimal vatAmount) { this.vatAmount = vatAmount; }

    public BigDecimal getRecargoRate() { return recargoRate; }
    public void setRecargoRate(BigDecimal recargoRate) { this.recargoRate = recargoRate; }

    public BigDecimal getRecargoAmount() { return recargoAmount; }
    public void setRecargoAmount(BigDecimal recargoAmount) { this.recargoAmount = recargoAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public boolean isRecargoApplied() { return recargoApplied; }
    public void setRecargoApplied(boolean recargoApplied) { this.recargoApplied = recargoApplied; }
}
