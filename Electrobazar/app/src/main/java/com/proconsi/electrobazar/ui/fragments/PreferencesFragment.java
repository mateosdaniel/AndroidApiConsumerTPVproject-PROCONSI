package com.proconsi.electrobazar.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.proconsi.electrobazar.R;
import com.proconsi.electrobazar.databinding.FragmentPreferencesBinding;
import com.proconsi.electrobazar.utils.ThemeManager;

/**
 * PreferencesFragment — mirrors web TPV preferences panel.
 *
 * Accent colors use separate keys per mode (dark_accent_idx / light_accent_idx)
 * just like the web app uses darkAccent / lightAccent separately.
 * Primary background also uses separate keys (dark_primary_idx / light_primary_idx).
 */
public class PreferencesFragment extends Fragment {

    private FragmentPreferencesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPreferencesBinding.inflate(inflater, container, false);

        setupThemePicker();
        setupAccentGrid();
        setupPrimaryGrid();
        setupFontPickers();
        ThemeManager.applyColorsToView(binding.getRoot(), requireContext());

        binding.btnReset.setOnClickListener(v -> {
            ThemeManager.reset(requireContext());
            requireActivity().recreate();
        });

        return binding.getRoot();
    }

    // ── Theme mode (dark / light) ─────────────────────────────────────────────
    private void setupThemePicker() {
        SharedPreferences prefs = prefs();
        String theme = prefs.getString(ThemeManager.KEY_THEME, "dark");

        if ("light".equals(theme)) binding.rbLight.setChecked(true);
        else binding.rbDark.setChecked(true);

        binding.rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            String mode = (checkedId == R.id.rbLight) ? "light" : "dark";
            ThemeManager.setPref(requireContext(), ThemeManager.KEY_THEME, mode);
            requireActivity().recreate();
        });
    }

    // ── Accent color swatches (8 colors, mode-dependent) ─────────────────────
    private void setupAccentGrid() {
        SharedPreferences prefs = prefs();
        boolean dark = ThemeManager.isDark(requireContext());
        String accentKey = dark ? ThemeManager.KEY_DARK_ACCENT : ThemeManager.KEY_LIGHT_ACCENT;
        int defaultAccent = dark ? 7 : 0; // web defaults: dark=cian, light=monochrome
        int currentIdx = prefs.getInt(accentKey, defaultAccent);

        String[] colors = ThemeManager.getAccentValues();
        binding.accentGrid.removeAllViews();

        for (int i = 0; i < colors.length; i++) {
            ImageView swatch = makeSwatch(44);

            // Monochrome (index 0): web rule — dark→white, light→black
            String colorHex = (i == 0)
                    ? (dark ? "#ffffff" : "#000000")
                    : colors[i];
            swatch.setColorFilter(Color.parseColor(colorHex));

            if (i == currentIdx) {
                swatch.setPadding(6, 6, 6, 6);
                swatch.setBackgroundResource(R.drawable.badge_active_yes);
            }

            final int index = i;
            swatch.setOnClickListener(v -> {
                ThemeManager.setPref(requireContext(), accentKey, index);
                requireActivity().recreate();
            });

            binding.accentGrid.addView(swatch);
        }
    }

    // ── Primary background swatches (3 tones per mode) ───────────────────────
    private void setupPrimaryGrid() {
        SharedPreferences prefs = prefs();
        boolean dark = ThemeManager.isDark(requireContext());
        String primaryKey = dark ? ThemeManager.KEY_DARK_PRIMARY : ThemeManager.KEY_LIGHT_PRIMARY;
        int currentIdx = prefs.getInt(primaryKey, 0);

        String[] colors = dark
                ? ThemeManager.getDarkPrimaryHex()
                : ThemeManager.getLightPrimaryHex();

        binding.primaryGrid.removeAllViews();

        for (int i = 0; i < colors.length; i++) {
            ImageView swatch = makeSwatch(50);
            swatch.setColorFilter(Color.parseColor(colors[i]));

            if (i == currentIdx) {
                swatch.setPadding(6, 6, 6, 6);
                swatch.setBackgroundResource(R.drawable.badge_active_yes);
            }

            final int index = i;
            swatch.setOnClickListener(v -> {
                ThemeManager.setPref(requireContext(), primaryKey, index);
                requireActivity().recreate();
            });

            binding.primaryGrid.addView(swatch);
        }
    }

    // ── Font size + type ──────────────────────────────────────────────────────
    private void setupFontPickers() {
        SharedPreferences prefs = prefs();

        // Font Size
        String fontSize = prefs.getString(ThemeManager.KEY_FONT_SIZE, "normal");
        switch (fontSize) {
            case "small":  binding.rbFontSizeSmall.setChecked(true); break;
            case "large":  binding.rbFontSizeLarge.setChecked(true); break;
            case "xl":     binding.rbFontSizeXL.setChecked(true); break;
            default:       binding.rbFontSizeNormal.setChecked(true); break;
        }
        binding.rgFontSize.setOnCheckedChangeListener((group, checkedId) -> {
            String size = "normal";
            if (checkedId == R.id.rbFontSizeSmall) size = "small";
            else if (checkedId == R.id.rbFontSizeLarge) size = "large";
            else if (checkedId == R.id.rbFontSizeXL) size = "xl";
            ThemeManager.setPref(requireContext(), ThemeManager.KEY_FONT_SIZE, size);
            ThemeManager.applyFontToView(requireActivity().getWindow().getDecorView(), requireContext());
        });

        // Font Type
        String fontType = prefs.getString(ThemeManager.KEY_FONT_TYPE, "barlow");
        switch (fontType) {
            case "montserrat": binding.rbFontModerno.setChecked(true); break;
            case "playfair":   binding.rbFontClasico.setChecked(true); break;
            case "quicksand":  binding.rbFontRounded.setChecked(true); break;
            default:           binding.rbFontBarlow.setChecked(true); break;
        }
        binding.rgFontType.setOnCheckedChangeListener((group, checkedId) -> {
            String type = "barlow";
            if (checkedId == R.id.rbFontModerno) type = "montserrat";
            else if (checkedId == R.id.rbFontClasico) type = "playfair";
            else if (checkedId == R.id.rbFontRounded) type = "quicksand";
            ThemeManager.setPref(requireContext(), ThemeManager.KEY_FONT_TYPE, type);
            ThemeManager.applyFontToView(requireActivity().getWindow().getDecorView(), requireContext());
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private SharedPreferences prefs() {
        return requireContext().getSharedPreferences(ThemeManager.PREFS_NAME, Context.MODE_PRIVATE);
    }

    private ImageView makeSwatch(int dpSize) {
        ImageView swatch = new ImageView(getContext());
        int size = (int) (dpSize * getResources().getDisplayMetrics().density);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = size;
        params.height = size;
        params.setMargins(12, 12, 12, 12);
        swatch.setLayoutParams(params);
        swatch.setImageResource(R.drawable.swatch_circle);
        return swatch;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
