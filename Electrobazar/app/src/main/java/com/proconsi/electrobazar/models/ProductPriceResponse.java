package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class ProductPriceResponse {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal vatRate;
    private String startDate;
    private String endDate;
    private String label;
    private String createdAt;
    private boolean currentlyActive;
    private BigDecimal priceChange;
    private BigDecimal priceChangePct;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isCurrentlyActive() { return currentlyActive; }
    public void setCurrentlyActive(boolean currentlyActive) { this.currentlyActive = currentlyActive; }

    public BigDecimal getPriceChange() { return priceChange; }
    public void setPriceChange(BigDecimal priceChange) { this.priceChange = priceChange; }

    public BigDecimal getPriceChangePct() { return priceChangePct; }
    public void setPriceChangePct(BigDecimal priceChangePct) { this.priceChangePct = priceChangePct; }
}
