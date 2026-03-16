package com.proconsi.electrobazar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;

/**
 * ThemeManager — mirrors the web app's localStorage-based theme system exactly.
 *
 * Palettes (identical to theme.html JS):
 *   accentColors[0..7]: monochrome, orange, blue, green, red, purple, pink, cyan
 *   darkP[0..2]:  Medianoche, Negro Total, Carbon
 *   lightP[0..2]: Blanco Puro, Arena, Ceniza
 *   lightFixed:   border=#dae1e7, muted=#64748b, text=#0f172a
 *
 * Prefs keys (matching web where possible):
 *   theme_mode        "dark" | "light"
 *   dark_primary_idx  0-2
 *   light_primary_idx 0-2
 *   dark_accent_idx   0-7
 *   light_accent_idx  0-7
 *   font_size         "small" | "normal" | "large" | "xl"
 *   font_type         "barlow" | "montserrat" | "playfair" | "quicksand"
 */
public class ThemeManager {

    public static final String PREFS_NAME = "tpv_preferences";

    // ── Pref keys ──────────────────────────────────────────────────────────
    public static final String KEY_THEME          = "theme_mode";
    public static final String KEY_DARK_PRIMARY   = "dark_primary_idx";
    public static final String KEY_LIGHT_PRIMARY  = "light_primary_idx";
    public static final String KEY_DARK_ACCENT    = "dark_accent_idx";
    public static final String KEY_LIGHT_ACCENT   = "light_accent_idx";
    /** Legacy key — migrated on first read */
    private static final String KEY_LEGACY_PRIMARY = "primary_idx";
    private static final String KEY_LEGACY_ACCENT  = "accent_idx";
    public static final String KEY_FONT_SIZE      = "font_size";
    public static final String KEY_FONT_TYPE      = "font_type";

    // ── Accent palette (index 0 = monochrome, resolved at runtime) ─────────
    private static final String[] ACCENT_VALUES = {
        "#ffffff", // 0 monochrome (dark: white, light: black)
        "#f5a623", // 1 naranja
        "#3b82f6", // 2 azul
        "#22c55e", // 3 verde
        "#ef4444", // 4 rojo
        "#a855f7", // 5 morado
        "#ec4899", // 6 rosa
        "#06b6d4"  // 7 cian
    };
    private static final String[] ACCENT_HOVER = {
        "#e0e0e0", // 0 monochrome dark hover (resolved at runtime for light)
        "#e09400",
        "#2563eb",
        "#16a34a",
        "#dc2626",
        "#9333ea",
        "#db2777",
        "#0891b2"
    };

    // ── Dark palettes ───────────────────────────────────────────────────────
    private static final int[][] DARK_PALETTES = {
        // primary, secondary, surface, border, muted, text
        { 0x151525, 0x1e1e35, 0x252545, 0x2c2c4d, 0x8892a4, 0xe8eaf0 }, // 0 Medianoche
        { 0x000000, 0x0c0c0c, 0x161616, 0x222222, 0x777777, 0xe0e0e0 }, // 1 Negro
        { 0x121212, 0x1a1a1a, 0x242424, 0x2d2d2d, 0x888888, 0xe8e8e8 }, // 2 Carbon
    };

    // ── Light palettes ──────────────────────────────────────────────────────
    private static final int[][] LIGHT_PALETTES = {
        // primary, secondary, surface  (border/muted/text are fixed for all light)
        { 0xffffff, 0xf8fafc, 0xf1f5f9 }, // 0 Blanco Puro
        { 0xf5edc5, 0xfaf3e0, 0xf0e6c8 }, // 1 Arena
        { 0xeef2f7, 0xe4e9f0, 0xd8e0ea }, // 2 Ceniza
    };
    private static final int LIGHT_BORDER = 0xdae1e7;
    private static final int LIGHT_MUTED  = 0x64748b;
    private static final int LIGHT_TEXT   = 0x0f172a;

    // ── Helper: make opaque int from 0xRRGGBB ──────────────────────────────
    private static int rgb(int hex) {
        return 0xFF000000 | hex;
    }

    // ── Pref helpers ────────────────────────────────────────────────────────
    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isDark(Context ctx) {
        return "dark".equals(prefs(ctx).getString(KEY_THEME, "dark"));
    }

    // Migrate legacy single-key prefs on first call
    private static SharedPreferences migratedPrefs(Context ctx) {
        SharedPreferences p = prefs(ctx);
        if (p.contains(KEY_LEGACY_PRIMARY) && !p.contains(KEY_DARK_PRIMARY)) {
            int idx = p.getInt(KEY_LEGACY_PRIMARY, 0);
            p.edit()
                .putInt(KEY_DARK_PRIMARY, idx)
                .putInt(KEY_LIGHT_PRIMARY, 0)
                .remove(KEY_LEGACY_PRIMARY)
                .apply();
        }
        if (p.contains(KEY_LEGACY_ACCENT) && !p.contains(KEY_DARK_ACCENT)) {
            int idx = p.getInt(KEY_LEGACY_ACCENT, 1);
            p.edit()
                .putInt(KEY_DARK_ACCENT, idx)
                .putInt(KEY_LIGHT_ACCENT, 0) // monochrome default for light (like web)
                .remove(KEY_LEGACY_ACCENT)
                .apply();
        }
        return p;
    }

    // ── Resolve current palette values ──────────────────────────────────────

    public static int getPrimaryColor(Context ctx) {
        SharedPreferences p = migratedPrefs(ctx);
        boolean dark = isDark(ctx);
        if (dark) {
            int idx = clamp(p.getInt(KEY_DARK_PRIMARY, 0), DARK_PALETTES.length);
            return rgb(DARK_PALETTES[idx][0]);
        } else {
            int idx = clamp(p.getInt(KEY_LIGHT_PRIMARY, 0), LIGHT_PALETTES.length);
            return rgb(LIGHT_PALETTES[idx][0]);
        }
    }

    public static int getSecondaryColor(Context ctx) {
        SharedPreferences p = migratedPrefs(ctx);
        boolean dark = isDark(ctx);
        if (dark) {
            int idx = clamp(p.getInt(KEY_DARK_PRIMARY, 0), DARK_PALETTES.length);
            return rgb(DARK_PALETTES[idx][1]);
        } else {
            int idx = clamp(p.getInt(KEY_LIGHT_PRIMARY, 0), LIGHT_PALETTES.length);
            return rgb(LIGHT_PALETTES[idx][1]);
        }
    }

    public static int getSurfaceColor(Context ctx) {
        SharedPreferences p = migratedPrefs(ctx);
        boolean dark = isDark(ctx);
        if (dark) {
            int idx = clamp(p.getInt(KEY_DARK_PRIMARY, 0), DARK_PALETTES.length);
            return rgb(DARK_PALETTES[idx][2]);
        } else {
            int idx = clamp(p.getInt(KEY_LIGHT_PRIMARY, 0), LIGHT_PALETTES.length);
            return rgb(LIGHT_PALETTES[idx][2]);
        }
    }

    public static int getBorderColor(Context ctx) {
        SharedPreferences p = migratedPrefs(ctx);
        boolean dark = isDark(ctx);
        if (dark) {
            int idx = clamp(p.getInt(KEY_DARK_PRIMARY, 0), DARK_PALETTES.length);
            return rgb(DARK_PALETTES[idx][3]);
        } else {
            return rgb(LIGHT_BORDER);
        }
    }

    public static int getMutedColor(Context ctx) {
        SharedPreferences p = migratedPrefs(ctx);
        boolean dark = isDark(ctx);
        if (dark) {
            int idx = clamp(p.getInt(KEY_DARK_PRIMARY, 0), DARK_PALETTES.length);
            return rgb(DARK_PALETTES[idx][4]);
        } else {
            return rgb(LIGHT_MUTED);
        }
    }

    public static int getTextColor(Context ctx) {
        boolean dark = isDark(ctx);
        if (dark) {
            SharedPreferences p = migratedPrefs(ctx);
            int idx = clamp(p.getInt(KEY_DARK_PRIMARY, 0), DARK_PALETTES.length);
            return rgb(DARK_PALETTES[idx][5]);
        } else {
            return rgb(LIGHT_TEXT);
        }
    }

    /** Resolved accent color (monochrome = white/black depending on mode) */
    public static int getAccentColor(Context ctx) {
        SharedPreferences p = migratedPrefs(ctx);
        boolean dark = isDark(ctx);
        String accentKey = dark ? KEY_DARK_ACCENT : KEY_LIGHT_ACCENT;
        int defaultAccent = dark ? 7 : 0; // web default: dark=cian(7), light=monochrome(0)
        int idx = clamp(p.getInt(accentKey, defaultAccent), ACCENT_VALUES.length);
        if (idx == 0) {
            return dark ? Color.WHITE : Color.BLACK;
        }
        return Color.parseColor(ACCENT_VALUES[idx]);
    }

    /** Resolved accent hover color */
    public static int getAccentHoverColor(Context ctx) {
        SharedPreferences p = migratedPrefs(ctx);
        boolean dark = isDark(ctx);
        String accentKey = dark ? KEY_DARK_ACCENT : KEY_LIGHT_ACCENT;
        int defaultAccent = dark ? 7 : 0;
        int idx = clamp(p.getInt(accentKey, defaultAccent), ACCENT_VALUES.length);
        if (idx == 0) {
            return dark ? Color.parseColor("#e0e0e0") : Color.parseColor("#333333");
        }
        return Color.parseColor(ACCENT_HOVER[idx]);
    }

    // ─── applyTheme: call BEFORE setContentView in every Activity ──────────

    /**
     * Applies the correct Android theme style (dark or light) and then
     * sets the window background + status/nav bar colors from the selected palette.
     * Must be called BEFORE super.onCreate() and setContentView().
     */
    public static void applyTheme(Activity activity) {
        SharedPreferences p = migratedPrefs(activity);
        boolean dark = "dark".equals(p.getString(KEY_THEME, "dark"));

        // Set the base Material theme (controls widget defaults)
        activity.setTheme(dark
                ? com.proconsi.electrobazar.R.style.Theme_Electrobazar_Dark
                : com.proconsi.electrobazar.R.style.Theme_Electrobazar_Light);

        // Override dynamic window colors from selected palette
        int primaryColor = getPrimaryColor(activity);
        activity.getWindow().setBackgroundDrawable(new ColorDrawable(primaryColor));
        activity.getWindow().setStatusBarColor(primaryColor);
        activity.getWindow().setNavigationBarColor(primaryColor);
    }

    /**
     * Call this AFTER setContentView() to push all dynamic colors into the
     * window's decor view hierarchy so every View reflects the correct palette.
     * This is the equivalent of the web's CSS variables being set inline.
     */
    public static void applyColors(Activity activity) {
        View root = activity.getWindow().getDecorView().getRootView();
        applyColorsToView(root, activity);
    }

    // ─── Per-view color application ─────────────────────────────────────────

    public static void applyColorsToView(View root, Context ctx) {
        int textColor   = getTextColor(ctx);
        int mutedColor  = getMutedColor(ctx);
        int primary     = getPrimaryColor(ctx);
        int surface     = getSurfaceColor(ctx);
        int secondary   = getSecondaryColor(ctx);

        applyTextColors(root, textColor, mutedColor, primary, surface, secondary, ctx);
    }

    private static void applyTextColors(View view, int text, int muted, int primary,
                                        int surface, int secondary, Context ctx) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            // Only override if view is using the default text color token
            // (avoids overwriting intentionally colored views like danger/success badges)
            int currentColor = tv.getCurrentTextColor();
            // If the color is exactly the old dark default text or old dark muted, update it
            if (isMaterialThemeTextColor(currentColor)) {
                tv.setTextColor(text);
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyTextColors(group.getChildAt(i), text, muted, primary, surface, secondary, ctx);
            }
        }
    }

    /** Colors assigned by the Material theme xml that we want to override */
    private static boolean isMaterialThemeTextColor(int color) {
        // Match typical Material dark/light theme text colors
        return color == 0xFFE8EAF0 // dark0 text
            || color == 0xFFE0E0E0 // dark1 text
            || color == 0xFFE8E8E8 // dark2 text
            || color == 0xFF0F172A // light text
            || color == 0xDE000000 // Material default black 87%
            || color == 0xFFFFFFFF // Material default white
            || color == 0xFF212121; // Material dark text
    }

    // ─── Font helpers ────────────────────────────────────────────────────────

    public static float getFontSize(Context ctx) {
        String size = prefs(ctx).getString(KEY_FONT_SIZE, "normal");
        switch (size) {
            case "small":  return 14f;
            case "large":  return 18f;
            case "xl":     return 20f;
            default:       return 16f;
        }
    }

    public static Typeface getFontTypeface(Context ctx) {
        String font = prefs(ctx).getString(KEY_FONT_TYPE, "barlow");
        switch (font) {
            case "playfair":  return Typeface.create("serif", Typeface.NORMAL);
            case "quicksand": return Typeface.create("sans-serif-light", Typeface.NORMAL);
            default:          return Typeface.create("sans-serif", Typeface.NORMAL);
        }
    }

    public static void applyFontToView(View view, Context ctx) {
        Typeface tf   = getFontTypeface(ctx);
        float size    = getFontSize(ctx);
        applyFontRecursive(view, tf, size);
    }

    private static void applyFontRecursive(View view, Typeface tf, float size) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(tf);
            ((TextView) view).setTextSize(size);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyFontRecursive(group.getChildAt(i), tf, size);
            }
        }
    }

    // ─── Pref setters ────────────────────────────────────────────────────────

    public static void setPref(Context ctx, String key, String value) {
        prefs(ctx).edit().putString(key, value).apply();
    }

    public static void setPref(Context ctx, String key, int value) {
        prefs(ctx).edit().putInt(key, value).apply();
    }

    public static void reset(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }

    // ─── Utility ─────────────────────────────────────────────────────────────

    private static int clamp(int value, int max) {
        if (value < 0 || value >= max) return 0;
        return value;
    }

    /** Returns the current accent index for the active mode (dark or light) */
    public static int getCurrentAccentIdx(Context ctx) {
        SharedPreferences p = migratedPrefs(ctx);
        boolean dark = isDark(ctx);
        int defaultAccent = dark ? 7 : 0;
        return clamp(p.getInt(dark ? KEY_DARK_ACCENT : KEY_LIGHT_ACCENT, defaultAccent), ACCENT_VALUES.length);
    }

    /** Returns the current primary palette index for the active mode */
    public static int getCurrentPrimaryIdx(Context ctx) {
        SharedPreferences p = migratedPrefs(ctx);
        boolean dark = isDark(ctx);
        int max = dark ? DARK_PALETTES.length : LIGHT_PALETTES.length;
        return clamp(p.getInt(dark ? KEY_DARK_PRIMARY : KEY_LIGHT_PRIMARY, 0), max);
    }

    /** All 8 accent hex strings, same order as web */
    public static String[] getAccentValues() {
        return ACCENT_VALUES.clone();
    }

    /** Dark palette primary hex strings (for swatches) */
    public static String[] getDarkPrimaryHex() {
        return new String[] { "#151525", "#000000", "#121212" };
    }

    /** Light palette primary hex strings (for swatches) */
    public static String[] getLightPrimaryHex() {
        return new String[] { "#ffffff", "#f5edc5", "#eef2f7" };
    }
}
