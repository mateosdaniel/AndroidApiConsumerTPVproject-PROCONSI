package com.example.electrobazar.models;

import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private Double price;

    @SerializedName("barcode")
    private String barcode;

    @SerializedName("stock")
    private Integer stock;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("active")
    private Boolean active;

    @SerializedName("category")
    private Category category;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public String getBarcode() { return barcode; }
    public Integer getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
    public Boolean getActive() { return active; }
    public Category getCategory() { return category; }
}