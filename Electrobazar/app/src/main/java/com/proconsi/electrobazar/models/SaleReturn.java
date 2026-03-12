package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class SaleReturn {
    private Long id;
    private String returnNumber;
    private Sale originalSale;
    private String createdAt;
    private Worker worker;
    private String reason;
    private String type; // TOTAL, PARTIAL
    private BigDecimal totalRefunded;
    private PaymentMethod paymentMethod;
    private String status; // COMPLETED, CANCELLED
    private List<ReturnLine> lines;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReturnNumber() { return returnNumber; }
    public void setReturnNumber(String returnNumber) { this.returnNumber = returnNumber; }

    public Sale getOriginalSale() { return originalSale; }
    public void setOriginalSale(Sale originalSale) { this.originalSale = originalSale; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getTotalRefunded() { return totalRefunded; }
    public void setTotalRefunded(BigDecimal totalRefunded) { this.totalRefunded = totalRefunded; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<ReturnLine> getLines() { return lines; }
    public void setLines(List<ReturnLine> lines) { this.lines = lines; }
}
