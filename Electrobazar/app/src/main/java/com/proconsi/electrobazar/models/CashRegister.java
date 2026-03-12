package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class CashRegister {
    private Long id;
    private String registerDate;
    private BigDecimal openingBalance;
    private BigDecimal cashSales;
    private BigDecimal cardSales;
    private BigDecimal totalSales;
    private BigDecimal closingBalance;
    private BigDecimal cashRefunds;
    private BigDecimal cardRefunds;
    private BigDecimal totalWithdrawals;
    private BigDecimal totalEntries;
    private BigDecimal difference;
    private String notes;
    private String openingTime;
    private String closedAt;
    private Boolean closed;
    private Worker worker;
    private BigDecimal retainedForNextShift;
    private Worker retainedByWorker;
    private List<CashWithdrawal> withdrawals;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegisterDate() { return registerDate; }
    public void setRegisterDate(String registerDate) { this.registerDate = registerDate; }

    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }

    public BigDecimal getCashSales() { return cashSales; }
    public void setCashSales(BigDecimal cashSales) { this.cashSales = cashSales; }

    public BigDecimal getCardSales() { return cardSales; }
    public void setCardSales(BigDecimal cardSales) { this.cardSales = cardSales; }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }

    public BigDecimal getClosingBalance() { return closingBalance; }
    public void setClosingBalance(BigDecimal closingBalance) { this.closingBalance = closingBalance; }

    public BigDecimal getCashRefunds() { return cashRefunds; }
    public void setCashRefunds(BigDecimal cashRefunds) { this.cashRefunds = cashRefunds; }

    public BigDecimal getCardRefunds() { return cardRefunds; }
    public void setCardRefunds(BigDecimal cardRefunds) { this.cardRefunds = cardRefunds; }

    public BigDecimal getTotalWithdrawals() { return totalWithdrawals; }
    public void setTotalWithdrawals(BigDecimal totalWithdrawals) { this.totalWithdrawals = totalWithdrawals; }

    public BigDecimal getTotalEntries() { return totalEntries; }
    public void setTotalEntries(BigDecimal totalEntries) { this.totalEntries = totalEntries; }

    public BigDecimal getDifference() { return difference; }
    public void setDifference(BigDecimal difference) { this.difference = difference; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getOpeningTime() { return openingTime; }
    public void setOpeningTime(String openingTime) { this.openingTime = openingTime; }

    public String getClosedAt() { return closedAt; }
    public void setClosedAt(String closedAt) { this.closedAt = closedAt; }

    public Boolean getClosed() { return closed; }
    public void setClosed(Boolean closed) { this.closed = closed; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public BigDecimal getRetainedForNextShift() { return retainedForNextShift; }
    public void setRetainedForNextShift(BigDecimal retainedForNextShift) { this.retainedForNextShift = retainedForNextShift; }

    public Worker getRetainedByWorker() { return retainedByWorker; }
    public void setRetainedByWorker(Worker retainedByWorker) { this.retainedByWorker = retainedByWorker; }

    public List<CashWithdrawal> getWithdrawals() { return withdrawals; }
    public void setWithdrawals(List<CashWithdrawal> withdrawals) { this.withdrawals = withdrawals; }
}
