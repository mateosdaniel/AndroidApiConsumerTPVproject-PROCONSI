package com.proconsi.electrobazar.models;

public class Invoice {
    private Long id;
    private String invoiceNumber;
    private String serie;
    private int year;
    private int sequenceNumber;
    private Sale sale;
    private String createdAt;
    private String status; // ACTIVE, RECTIFIED
    private Invoice rectifiedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Invoice getRectifiedBy() { return rectifiedBy; }
    public void setRectifiedBy(Invoice rectifiedBy) { this.rectifiedBy = rectifiedBy; }
}
