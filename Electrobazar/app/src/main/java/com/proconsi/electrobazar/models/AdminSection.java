package com.proconsi.electrobazar.models;

public class AdminSection {
    private final int id;
    private final String title;
    private final int iconRes;

    public AdminSection(int id, String title, int iconRes) {
        this.id = id;
        this.title = title;
        this.iconRes = iconRes;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getIconRes() { return iconRes; }
}
