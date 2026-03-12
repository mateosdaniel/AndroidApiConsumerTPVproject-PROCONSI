package com.proconsi.electrobazar.models;

import java.util.List;

public class SuspendedSale {
    private Long id;
    private String createdAt;
    private String updatedAt;
    private Worker worker;
    private String label;
    private String status; // SUSPENDED, RESUMED, CANCELLED
    private List<SuspendedSaleLine> lines;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<SuspendedSaleLine> getLines() { return lines; }
    public void setLines(List<SuspendedSaleLine> lines) { this.lines = lines; }
}
