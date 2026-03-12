package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class CashRegisterOpenSuggestion {
    private boolean hasSuggestion;
    private BigDecimal suggestedBalance;

    public boolean isHasSuggestion() { return hasSuggestion; }
    public void setHasSuggestion(boolean hasSuggestion) { this.hasSuggestion = hasSuggestion; }

    public BigDecimal getSuggestedBalance() { return suggestedBalance; }
    public void setSuggestedBalance(BigDecimal suggestedBalance) { this.suggestedBalance = suggestedBalance; }
}
