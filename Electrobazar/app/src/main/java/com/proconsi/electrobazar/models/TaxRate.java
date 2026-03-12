package com.proconsi.electrobazar.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TaxRate {
    private Long id;
    private BigDecimal vatRate;
    private BigDecimal reRate;
    private String description;
    private Boolean active;
    private String validFrom;
    private String validTo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getVatRate() { return vatRate; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }

    public BigDecimal getReRate() { return reRate; }
    public void setReRate(BigDecimal reRate) { this.reRate = reRate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getValidFrom() { return validFrom; }
    public void setValidFrom(String validFrom) { this.validFrom = validFrom; }

    public String getValidTo() { return validTo; }
    public void setValidTo(String validTo) { this.validTo = validTo; }
}
