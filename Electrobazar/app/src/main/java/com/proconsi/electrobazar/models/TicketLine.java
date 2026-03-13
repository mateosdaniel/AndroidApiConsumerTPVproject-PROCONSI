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
        // The ViewModel will call updateTotals with the correct RE status
    }

    public BigDecimal getUnitPrice() {
        return unitPrice != null ? unitPrice : product.getPrice();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        // The ViewModel will call updateTotals with the correct RE status
    }

    public BigDecimal getLineTotal() {
        return lineTotal != null ? lineTotal : BigDecimal.ZERO;
    }

    public void updateTotals(boolean applyRE) {
        BigDecimal grossPrice = getUnitPrice();
        if (grossPrice == null) {
            this.lineTotal = BigDecimal.ZERO;
            return;
        }

        BigDecimal vatRate = new BigDecimal("0.21");
        BigDecimal reRate = BigDecimal.ZERO;

        if (product.getTaxRate() != null) {
            if (product.getTaxRate().getVatRate() != null) {
                vatRate = product.getTaxRate().getVatRate();
            }
            if (applyRE && product.getTaxRate().getReRate() != null) {
                reRate = product.getTaxRate().getReRate();
            }
        }

        java.math.RoundingMode rounding = java.math.RoundingMode.HALF_UP;
        
        // Exact formula from backend (SaleServiceImpl.java & RecargoEquivalenciaCalculator.java):
        // 1. netUnitPrice = grossPrice / (1 + vatRate) (10 decimal places)
        // 2. baseAmount = (netUnitPrice * quantity).setScale(2, HALF_UP)
        // 3. reAmount = (baseAmount * reRate).setScale(2, HALF_UP)
        // 4. lineTotal = (grossPrice * quantity).setScale(2, HALF_UP) + reAmount
        
        BigDecimal divisor = BigDecimal.ONE.add(vatRate);
        BigDecimal netUnitPrice = grossPrice.divide(divisor, 10, rounding);
        BigDecimal baseAmount = netUnitPrice.multiply(new BigDecimal(quantity)).setScale(2, rounding);
        BigDecimal vatAmount = baseAmount.multiply(vatRate).setScale(2, rounding);
        BigDecimal recargoAmount = baseAmount.multiply(reRate).setScale(2, rounding);

        // Total = Base + VAT + RE (exactly as the backend calculates it)
        this.lineTotal = baseAmount.add(vatAmount).add(recargoAmount).setScale(2, rounding);
    }
}
