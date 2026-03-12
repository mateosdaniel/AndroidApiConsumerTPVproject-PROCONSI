package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class CashWithdrawal {
    private Long id;
    private CashRegister cashRegister;
    private BigDecimal amount;
    private String reason;
    private Worker worker;
    private String createdAt;
    private String type; // WITHDRAWAL, ENTRY

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CashRegister getCashRegister() { return cashRegister; }
    public void setCashRegister(CashRegister cashRegister) { this.cashRegister = cashRegister; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
