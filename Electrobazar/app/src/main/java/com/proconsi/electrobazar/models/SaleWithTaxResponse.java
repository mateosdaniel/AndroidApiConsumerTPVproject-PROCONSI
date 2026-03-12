package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class SaleWithTaxResponse {
    private Long saleId;
    private String createdAt;
    private Long customerId;
    private String customerName;
    private boolean recargoEquivalenciaApplied;
    private PaymentMethod paymentMethod;
    private BigDecimal receivedAmount;
    private BigDecimal changeAmount;
    private List<TaxBreakdown> lines;
    private BigDecimal totalBase;
    private BigDecimal totalVat;
    private BigDecimal totalRecargo;
    private BigDecimal grandTotal;
    private String notes;

    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public boolean isRecargoEquivalenciaApplied() { return recargoEquivalenciaApplied; }
    public void setRecargoEquivalenciaApplied(boolean recargoEquivalenciaApplied) { this.recargoEquivalenciaApplied = recargoEquivalenciaApplied; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getReceivedAmount() { return receivedAmount; }
    public void setReceivedAmount(BigDecimal receivedAmount) { this.receivedAmount = receivedAmount; }

    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }

    public List<TaxBreakdown> getLines() { return lines; }
    public void setLines(List<TaxBreakdown> lines) { this.lines = lines; }

    public BigDecimal getTotalBase() { return totalBase; }
    public void setTotalBase(BigDecimal totalBase) { this.totalBase = totalBase; }

    public BigDecimal getTotalVat() { return totalVat; }
    public void setTotalVat(BigDecimal totalVat) { this.totalVat = totalVat; }

    public BigDecimal getTotalRecargo() { return totalRecargo; }
    public void setTotalRecargo(BigDecimal totalRecargo) { this.totalRecargo = totalRecargo; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
