package com.example.electrobazar.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Sale {
    @SerializedName("id")
    private Long id;

    @SerializedName("totalAmount")
    private Double totalAmount;

    @SerializedName("paymentMethod")
    private String paymentMethod; // CASH, CARD

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("lines")
    private List<SaleLine> lines;

    @SerializedName("customer")
    private Customer customer;

    @SerializedName("notes")
    private String notes;

    public Long getId() {
        return id;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<SaleLine> getLines() {
        return lines;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setLines(List<SaleLine> lines) {
        this.lines = lines;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
