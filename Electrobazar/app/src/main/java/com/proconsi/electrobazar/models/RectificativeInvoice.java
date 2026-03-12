package com.proconsi.electrobazar.models;

public class RectificativeInvoice {
    private Long id;
    private String rectificativeNumber;
    private SaleReturn saleReturn;
    private Invoice originalInvoice;
    private String createdAt;
    private String reason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRectificativeNumber() { return rectificativeNumber; }
    public void setRectificativeNumber(String rectificativeNumber) { this.rectificativeNumber = rectificativeNumber; }

    public SaleReturn getSaleReturn() { return saleReturn; }
    public void setSaleReturn(SaleReturn saleReturn) { this.saleReturn = saleReturn; }

    public Invoice getOriginalInvoice() { return originalInvoice; }
    public void setOriginalInvoice(Invoice originalInvoice) { this.originalInvoice = originalInvoice; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
