package com.example.electrobazar.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CashRegister {
    @SerializedName("id")
    private Long id;

    @SerializedName("openingDate")
    private String openingDate;

    @SerializedName("closingDate")
    private String closingDate;

    @SerializedName("openingBalance")
    private Double openingBalance;

    @SerializedName("closingBalance")
    private Double closingBalance;

    @SerializedName("totalCashSales")
    private Double totalCashSales;

    @SerializedName("totalCardSales")
    private Double totalCardSales;

    @SerializedName("expectedBalance")
    private Double expectedBalance;

    @SerializedName("difference")
    private Double difference;

    @SerializedName("notes")
    private String notes;

    @SerializedName("status")
    private String status; // OPEN, CLOSED

    public Long getId() {
        return id;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public Double getOpeningBalance() {
        return openingBalance;
    }

    public Double getClosingBalance() {
        return closingBalance;
    }

    public Double getTotalCashSales() {
        return totalCashSales;
    }

    public Double getTotalCardSales() {
        return totalCardSales;
    }

    public Double getExpectedBalance() {
        return expectedBalance;
    }

    public Double getDifference() {
        return difference;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public void setOpeningBalance(Double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public void setClosingBalance(Double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public void setTotalCashSales(Double totalCashSales) {
        this.totalCashSales = totalCashSales;
    }

    public void setTotalCardSales(Double totalCardSales) {
        this.totalCardSales = totalCardSales;
    }

    public void setExpectedBalance(Double expectedBalance) {
        this.expectedBalance = expectedBalance;
    }

    public void setDifference(Double difference) {
        this.difference = difference;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
