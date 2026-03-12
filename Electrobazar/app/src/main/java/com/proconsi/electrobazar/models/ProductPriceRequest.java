package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class ProductPriceRequest {
    private BigDecimal price;
    private BigDecimal vatRate;
    private String startDate;
    private String label;

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
