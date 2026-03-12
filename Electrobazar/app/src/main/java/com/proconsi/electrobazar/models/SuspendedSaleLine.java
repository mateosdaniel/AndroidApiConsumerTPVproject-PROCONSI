package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class SuspendedSaleLine {
    private Long id;
    private Product product;
    private Integer quantity;
    private BigDecimal unitPrice;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
