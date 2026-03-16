package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class Tariff {
    private Long id;
    private String name;
    private BigDecimal discountPercentage;
    private String description;
    private Boolean active;
    private Boolean systemTariff;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getSystemTariff() { return systemTariff; }
    public void setSystemTariff(Boolean systemTariff) { this.systemTariff = systemTariff; }
}
