package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class SaleLine {
    private Long id;
    private Product product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal originalUnitPrice;
    private BigDecimal discountPercentage;
    private BigDecimal basePriceNet;
    private BigDecimal vatRate;
    private BigDecimal subtotal;
    private BigDecimal baseAmount;
    private BigDecimal vatAmount;
    private BigDecimal recargoRate;
    private BigDecimal recargoAmount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getOriginalUnitPrice() { return originalUnitPrice; }
    public void setOriginalUnitPrice(BigDecimal originalUnitPrice) { this.originalUnitPrice = originalUnitPrice; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getBasePriceNet() { return basePriceNet; }
    public void setBasePriceNet(BigDecimal basePriceNet) { this.basePriceNet = basePriceNet; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }

    public BigDecimal getVatAmount() { return vatAmount; }
    public void setVatAmount(BigDecimal vatAmount) { this.vatAmount = vatAmount; }

    public BigDecimal getRecargoRate() { return recargoRate; }
    public void setRecargoRate(BigDecimal recargoRate) { this.recargoRate = recargoRate; }

    public BigDecimal getRecargoAmount() { return recargoAmount; }
    public void setRecargoAmount(BigDecimal recargoAmount) { this.recargoAmount = recargoAmount; }
}
