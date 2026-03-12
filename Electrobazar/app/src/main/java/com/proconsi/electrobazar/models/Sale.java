package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class Sale {
    private Long id;
    private String createdAt;
    private Customer customer;
    private Worker worker;
    private PaymentMethod paymentMethod;
    private BigDecimal totalAmount;
    private BigDecimal receivedAmount;
    private BigDecimal changeAmount;
    private BigDecimal totalBase;
    private BigDecimal totalVat;
    private BigDecimal totalRecargo;
    private String notes;
    private List<SaleLine> lines;
    private boolean applyRecargo;
    private String status; // ACTIVE, CANCELLED
    private String appliedTariff;
    private BigDecimal appliedDiscountPercentage;
    private BigDecimal totalDiscount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getReceivedAmount() { return receivedAmount; }
    public void setReceivedAmount(BigDecimal receivedAmount) { this.receivedAmount = receivedAmount; }

    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }

    public BigDecimal getTotalBase() { return totalBase; }
    public void setTotalBase(BigDecimal totalBase) { this.totalBase = totalBase; }

    public BigDecimal getTotalVat() { return totalVat; }
    public void setTotalVat(BigDecimal totalVat) { this.totalVat = totalVat; }

    public BigDecimal getTotalRecargo() { return totalRecargo; }
    public void setTotalRecargo(BigDecimal totalRecargo) { this.totalRecargo = totalRecargo; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<SaleLine> getLines() { return lines; }
    public void setLines(List<SaleLine> lines) { this.lines = lines; }

    public boolean isApplyRecargo() { return applyRecargo; }
    public void setApplyRecargo(boolean applyRecargo) { this.applyRecargo = applyRecargo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAppliedTariff() { return appliedTariff; }
    public void setAppliedTariff(String appliedTariff) { this.appliedTariff = appliedTariff; }

    public BigDecimal getAppliedDiscountPercentage() { return appliedDiscountPercentage; }
    public void setAppliedDiscountPercentage(BigDecimal appliedDiscountPercentage) { this.appliedDiscountPercentage = appliedDiscountPercentage; }

    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(BigDecimal totalDiscount) { this.totalDiscount = totalDiscount; }
}
