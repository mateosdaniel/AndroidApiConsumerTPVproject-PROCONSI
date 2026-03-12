package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class SaleWithTaxRequest {
    private Long customerId;
    private PaymentMethod paymentMethod;
    private String notes;
    private BigDecimal receivedAmount;
    private Long workerId;
    private List<SaleLineRequest> lines;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getReceivedAmount() { return receivedAmount; }
    public void setReceivedAmount(BigDecimal receivedAmount) { this.receivedAmount = receivedAmount; }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public List<SaleLineRequest> getLines() { return lines; }
    public void setLines(List<SaleLineRequest> lines) { this.lines = lines; }


    public static class SaleLineRequest {
        private Long productId;
        private Integer quantity;
        private BigDecimal overridePrice;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getOverridePrice() { return overridePrice; }
        public void setOverridePrice(BigDecimal overridePrice) { this.overridePrice = overridePrice; }
    }
}
