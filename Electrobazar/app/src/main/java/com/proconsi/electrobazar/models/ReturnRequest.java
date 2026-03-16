package com.proconsi.electrobazar.models;

import java.util.List;

public class ReturnRequest {
    private Long saleId;
    private List<ReturnLineRequest> lines;
    private String reason;
    private PaymentMethod paymentMethod;
    
    public ReturnRequest() {}

    public ReturnRequest(Long saleId, List<ReturnLineRequest> lines, String reason, PaymentMethod paymentMethod) {
        this.saleId = saleId;
        this.lines = lines;
        this.reason = reason;
        this.paymentMethod = paymentMethod;
    }

    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }

    public List<ReturnLineRequest> getLines() { return lines; }
    public void setLines(List<ReturnLineRequest> lines) { this.lines = lines; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}
