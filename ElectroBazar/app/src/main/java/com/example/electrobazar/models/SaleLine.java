package com.example.electrobazar.models;

import com.google.gson.annotations.SerializedName;

public class SaleLine {
    @SerializedName("id")
    private Long id;

    @SerializedName("product")
    private Product product;

    @SerializedName("quantity")
    private Integer quantity;

    @SerializedName("unitPrice")
    private Double unitPrice;

    @SerializedName("subtotal")
    private Double subtotal;

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
