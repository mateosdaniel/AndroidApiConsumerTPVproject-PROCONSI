package com.proconsi.electrobazar.models;

public class CashWithdrawalRequest {
    private String amount;
    private String reason;
    private String type; // WITHDRAWAL, ENTRY

    public CashWithdrawalRequest() {}

    public CashWithdrawalRequest(String amount, String reason, String type) {
        this.amount = amount;
        this.reason = reason;
        this.type = type;
    }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
