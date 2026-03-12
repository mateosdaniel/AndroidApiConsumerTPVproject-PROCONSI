package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class ReturnLine {
    private Long id;
    private SaleLine saleLine;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private BigDecimal vatRate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SaleLine getSaleLine() { return saleLine; }
    public void setSaleLine(SaleLine saleLine) { this.saleLine = saleLine; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }
}
