package com.proconsi.electrobazar.models;

public class Ticket {
    private Long id;
    private String ticketNumber;
    private String serie;
    private int year;
    private int sequenceNumber;
    private String createdAt;
    private boolean applyRecargo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isApplyRecargo() { return applyRecargo; }
    public void setApplyRecargo(boolean applyRecargo) { this.applyRecargo = applyRecargo; }
}
