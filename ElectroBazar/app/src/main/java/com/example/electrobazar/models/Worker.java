package com.example.electrobazar.models;

import com.google.gson.annotations.SerializedName;
import java.util.Set;

public class Worker {
    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("permissions")
    private Set<String> permissions;

    @SerializedName("active")
    private boolean active;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public boolean isActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
