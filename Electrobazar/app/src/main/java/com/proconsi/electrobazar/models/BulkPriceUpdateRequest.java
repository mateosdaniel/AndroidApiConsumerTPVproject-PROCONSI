package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class BulkPriceUpdateRequest {
    private List<Long> productIds;
    private BigDecimal percentage;
    private BigDecimal fixedAmount;
    private String effectiveDate;
    private String label;
    private BigDecimal vatRate;
    private List<Long> tariffIds;

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

    public BigDecimal getFixedAmount() { return fixedAmount; }
    public void setFixedAmount(BigDecimal fixedAmount) { this.fixedAmount = fixedAmount; }

    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }

    public List<Long> getTariffIds() { return tariffIds; }
    public void setTariffIds(List<Long> tariffIds) { this.tariffIds = tariffIds; }
}
