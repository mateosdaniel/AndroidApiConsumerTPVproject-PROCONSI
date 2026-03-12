package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class Category {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private BigDecimal ivaRate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public BigDecimal getIvaRate() { return ivaRate; }
    public void setIvaRate(BigDecimal ivaRate) { this.ivaRate = ivaRate; }
}
