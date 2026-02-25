package com.example.electrobazar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class ThemeManager {

    private static final String PREFS_NAME = "tpv-prefs";
    private static final String KEY_MODE = "mode";
    private static final String KEY_DARK_ACCENT = "darkAccent";
    private static final String KEY_LIGHT_ACCENT = "lightAccent";
    private static final String KEY_DARK_PRIMARY = "darkPrimaryIdx";
    private static final String KEY_LIGHT_PRIMARY = "lightPrimaryIdx";

    // Acentos: {color, hover}
    private static final int[][] ACCENT_COLORS = {
            {Color.parseColor("#f5a623"), Color.parseColor("#e09400")}, // Naranja
            {Color.parseColor("#3b82f6"), Color.parseColor("#2563eb")}, // Azul
            {Color.parseColor("#22c55e"), Color.parseColor("#16a34a")}, // Verde
            {Color.parseColor("#ef4444"), Color.parseColor("#dc2626")}, // Rojo
            {Color.parseColor("#a855f7"), Color.parseColor("#9333ea")}, // Violeta
            {Color.parseColor("#ec4899"), Color.parseColor("#db2777")}, // Rosa
            {Color.parseColor("#06b6d4"), Color.parseColor("#0891b2")}  // Cian
    };

    // Fondos oscuros: {primary, secondary}
    private static final int[][] DARK_PRIMARIES = {
            {Color.parseColor("#1a1a2e"), Color.parseColor("#16213e")}, // Medianoche
            {Color.parseColor("#000000"), Color.parseColor("#111111")}, // Negro total
            {Color.parseColor("#1c1c1c"), Color.parseColor("#2a2a2a")}  // Carbón
    };

    // Fondos claros: {primary, secondary}
    private static final int[][] LIGHT_PRIMARIES = {
            {Color.parseColor("#f6f6f6"), Color.parseColor("#ffffff")}, // Blanco total
            {Color.parseColor("#fbf9f6"), Color.parseColor("#fdfdfc")}, // Blanco hueso
            {Color.parseColor("#f0f4f8"), Color.parseColor("#ffffff")}  // Gris perla
    };

    private static final int LIGHT_BORDER    = Color.parseColor("#c8c8c8");
    private static final int LIGHT_TEXT_MUTED = Color.parseColor("#6b7280");
    private static final int LIGHT_TEXT_MAIN  = Color.parseColor("#1e293b");

    private static final int DARK_BORDER     = Color.parseColor("#1e2d45");
    private static final int DARK_TEXT_MUTED = Color.parseColor("#8892a4");
    private static final int DARK_TEXT_MAIN  = Color.parseColor("#e8eaf0");

    private final SharedPreferences prefs;

    public ThemeManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isDarkMode() {
        return "dark".equals(prefs.getString(KEY_MODE, "dark"));
    }

    public int getAccentColor() {
        int idx = isDarkMode()
                ? prefs.getInt(KEY_DARK_ACCENT, 6)
                : prefs.getInt(KEY_LIGHT_ACCENT, 0);
        idx = Math.min(idx, ACCENT_COLORS.length - 1);
        return ACCENT_COLORS[idx][0];
    }

    public int getPrimaryColor() {
        int[][] palette = isDarkMode() ? DARK_PRIMARIES : LIGHT_PRIMARIES;
        int idx = isDarkMode()
                ? prefs.getInt(KEY_DARK_PRIMARY, 0)
                : prefs.getInt(KEY_LIGHT_PRIMARY, 0);
        idx = Math.min(idx, palette.length - 1);
        return palette[idx][0];
    }

    public int getSecondaryColor() {
        int[][] palette = isDarkMode() ? DARK_PRIMARIES : LIGHT_PRIMARIES;
        int idx = isDarkMode()
                ? prefs.getInt(KEY_DARK_PRIMARY, 0)
                : prefs.getInt(KEY_LIGHT_PRIMARY, 0);
        idx = Math.min(idx, palette.length - 1);
        return palette[idx][1];
    }

    public int getTextMainColor() {
        return isDarkMode() ? DARK_TEXT_MAIN : LIGHT_TEXT_MAIN;
    }

    public int getTextMutedColor() {
        return isDarkMode() ? DARK_TEXT_MUTED : LIGHT_TEXT_MUTED;
    }

    public int getBorderColor() {
        return isDarkMode() ? DARK_BORDER : LIGHT_BORDER;
    }
}