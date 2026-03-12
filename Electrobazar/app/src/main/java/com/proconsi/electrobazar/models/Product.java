package com.proconsi.electrobazar.models;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal basePriceNet;
    private Integer stock;
    private Boolean active;
    private String imageUrl;
    private Category category;
    private TaxRate taxRate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getBasePriceNet() { return basePriceNet; }
    public void setBasePriceNet(BigDecimal basePriceNet) { this.basePriceNet = basePriceNet; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public TaxRate getTaxRate() { return taxRate; }
    public void setTaxRate(TaxRate taxRate) { this.taxRate = taxRate; }
}
