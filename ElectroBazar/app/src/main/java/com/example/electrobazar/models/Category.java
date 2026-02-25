package com.example.electrobazar.models;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("active")
    private Boolean active;

    // Constructor especial para el botón "Todos"
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
        this.active = true;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Boolean getActive() { return active; }
}