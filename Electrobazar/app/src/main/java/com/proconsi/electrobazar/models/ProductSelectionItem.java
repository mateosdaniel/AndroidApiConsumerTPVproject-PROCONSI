package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class ProductSelectionItem {
    private Long id;
    private String name;
    private BigDecimal currentPrice;
    private BigDecimal currentVat;
    private Long categoryId;
    private String categoryName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getCurrentVat() { return currentVat; }
    public void setCurrentVat(BigDecimal currentVat) { this.currentVat = currentVat; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
