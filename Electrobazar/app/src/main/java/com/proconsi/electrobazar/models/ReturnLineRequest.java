package com.proconsi.electrobazar.models;

public class ReturnLineRequest {
    private Long saleLineId;
    private int quantity;
    
    public ReturnLineRequest() {}

    public ReturnLineRequest(Long saleLineId, int quantity) {
        this.saleLineId = saleLineId;
        this.quantity = quantity;
    }

    public Long getSaleLineId() { return saleLineId; }
    public void setSaleLineId(Long saleLineId) { this.saleLineId = saleLineId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
