package com.proconsi.electrobazar.models;

public class ReturnCheckResponse {
    private Long saleId;
    private boolean canReturn;

    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }

    public boolean isCanReturn() { return canReturn; }
    public void setCanReturn(boolean canReturn) { this.canReturn = canReturn; }
}
