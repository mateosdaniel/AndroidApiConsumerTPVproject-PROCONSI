package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class TariffPriceEntryDTO {
    private Long productId;
    private String productName;
    private String categoryName;
    private BigDecimal basePrice;
    private BigDecimal netPrice;
    private BigDecimal vatRate;
    private BigDecimal priceWithVat;
    private BigDecimal reRate;
    private BigDecimal priceWithRe;
    private BigDecimal discountPercent;
    private String validFrom;
    private String validTo;
    private BigDecimal vatAmount;
    private BigDecimal reAmount;
    private boolean isFromHistory;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public BigDecimal getNetPrice() { return netPrice; }
    public void setNetPrice(BigDecimal netPrice) { this.netPrice = netPrice; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }

    public BigDecimal getPriceWithVat() { return priceWithVat; }
    public void setPriceWithVat(BigDecimal priceWithVat) { this.priceWithVat = priceWithVat; }

    public BigDecimal getReRate() { return reRate; }
    public void setReRate(BigDecimal reRate) { this.reRate = reRate; }

    public BigDecimal getPriceWithRe() { return priceWithRe; }
    public void setPriceWithRe(BigDecimal priceWithRe) { this.priceWithRe = priceWithRe; }

    public BigDecimal getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(BigDecimal discountPercent) { this.discountPercent = discountPercent; }

    public String getValidFrom() { return validFrom; }
    public void setValidFrom(String validFrom) { this.validFrom = validFrom; }

    public String getValidTo() { return validTo; }
    public void setValidTo(String validTo) { this.validTo = validTo; }

    public BigDecimal getVatAmount() { return vatAmount; }
    public void setVatAmount(BigDecimal vatAmount) { this.vatAmount = vatAmount; }

    public BigDecimal getReAmount() { return reAmount; }
    public void setReAmount(BigDecimal reAmount) { this.reAmount = reAmount; }

    public boolean isFromHistory() { return isFromHistory; }
    public void setFromHistory(boolean isFromHistory) { this.isFromHistory = isFromHistory; }
}
