package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.util.List;

public class CashCloseInfoDTO {
    private BigDecimal totalToday;
    private long countToday;
    private BigDecimal cardSalesToday;
    private BigDecimal cardRefundsToday;
    private BigDecimal cashSalesToday;
    private BigDecimal cashRefundsToday;
    private BigDecimal totalEntries;
    private BigDecimal totalWithdrawals;
    private BigDecimal expectedCashInDrawer;
    private long cancelledCount;
    private BigDecimal cancelledTotal;
    private List<SaleReturn> returnsToday;
    private BigDecimal openingBalance;
    private CashRegister todayRegister;

    public BigDecimal getTotalToday() { return totalToday; }
    public void setTotalToday(BigDecimal totalToday) { this.totalToday = totalToday; }

    public long getCountToday() { return countToday; }
    public void setCountToday(long countToday) { this.countToday = countToday; }

    public BigDecimal getCardSalesToday() { return cardSalesToday; }
    public void setCardSalesToday(BigDecimal cardSalesToday) { this.cardSalesToday = cardSalesToday; }

    public BigDecimal getCardRefundsToday() { return cardRefundsToday; }
    public void setCardRefundsToday(BigDecimal cardRefundsToday) { this.cardRefundsToday = cardRefundsToday; }

    public BigDecimal getCashSalesToday() { return cashSalesToday; }
    public void setCashSalesToday(BigDecimal cashSalesToday) { this.cashSalesToday = cashSalesToday; }

    public BigDecimal getCashRefundsToday() { return cashRefundsToday; }
    public void setCashRefundsToday(BigDecimal cashRefundsToday) { this.cashRefundsToday = cashRefundsToday; }

    public BigDecimal getTotalEntries() { return totalEntries; }
    public void setTotalEntries(BigDecimal totalEntries) { this.totalEntries = totalEntries; }

    public BigDecimal getTotalWithdrawals() { return totalWithdrawals; }
    public void setTotalWithdrawals(BigDecimal totalWithdrawals) { this.totalWithdrawals = totalWithdrawals; }

    public BigDecimal getExpectedCashInDrawer() { return expectedCashInDrawer; }
    public void setExpectedCashInDrawer(BigDecimal expectedCashInDrawer) { this.expectedCashInDrawer = expectedCashInDrawer; }

    public long getCancelledCount() { return cancelledCount; }
    public void setCancelledCount(long cancelledCount) { this.cancelledCount = cancelledCount; }

    public BigDecimal getCancelledTotal() { return cancelledTotal; }
    public void setCancelledTotal(BigDecimal cancelledTotal) { this.cancelledTotal = cancelledTotal; }

    public List<SaleReturn> getReturnsToday() { return returnsToday; }
    public void setReturnsToday(List<SaleReturn> returnsToday) { this.returnsToday = returnsToday; }

    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }

    public CashRegister getTodayRegister() { return todayRegister; }
    public void setTodayRegister(CashRegister todayRegister) { this.todayRegister = todayRegister; }
}
