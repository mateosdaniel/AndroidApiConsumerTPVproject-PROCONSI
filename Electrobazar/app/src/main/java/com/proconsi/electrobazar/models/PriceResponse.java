package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class PriceResponse {
    private BigDecimal price; // Gross price with VAT
    private BigDecimal priceWithRe; // Gross price with VAT + RE
    private BigDecimal basePrice; // Gross price without discount
    private BigDecimal discountAmount;

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getPriceWithRe() { return priceWithRe; }
    public void setPriceWithRe(BigDecimal priceWithRe) { this.priceWithRe = priceWithRe; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
}
