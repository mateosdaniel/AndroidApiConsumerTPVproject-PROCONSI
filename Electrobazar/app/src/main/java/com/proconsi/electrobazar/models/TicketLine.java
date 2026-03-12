package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class TicketLine {
    private Product product;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public TicketLine(Product product, int quantity) {
        this.product = product;
        this.setQuantity(quantity);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateTotals();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice != null ? unitPrice : product.getPrice();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        updateTotals();
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    private void updateTotals() {
        BigDecimal price = getUnitPrice();
        if (price != null) {
            this.lineTotal = price.multiply(new BigDecimal(quantity));
        } else {
            this.lineTotal = BigDecimal.ZERO;
        }
    }
}
