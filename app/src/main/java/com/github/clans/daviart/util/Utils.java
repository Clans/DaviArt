package com.github.clans.daviart.util;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;

public class Utils {

    private Utils() {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static RippleDrawable createRipple(@NonNull Palette.Swatch swatch,
                                              @IntRange(from = 0x0, to = 0xFF) int darkAlpha,
                                              @IntRange(from = 0x0, to = 0xFF) int lightAlpha,
                                              boolean bounded) {
        int rippleColor;
        if (isDark(swatch)) {
            rippleColor = ColorUtils.setAlphaComponent(swatch.getRgb(), darkAlpha);
        } else {
            rippleColor = ColorUtils.setAlphaComponent((swatch.getRgb()), lightAlpha);
        }

        return new RippleDrawable(ColorStateList.valueOf(rippleColor), null,
                bounded ? new ColorDrawable(Color.WHITE) : null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static RippleDrawable createRipple(@NonNull Palette palette,
                                              @IntRange(from = 0x0, to = 0xFF) int darkAlpha,
                                              @IntRange(from = 0x0, to = 0xFF) int lightAlpha,
                                              @ColorInt int fallbackColor,
                                              boolean bounded) {
        int rippleColor = fallbackColor;
        // try the named swatches in preference order
        if (palette.getVibrantSwatch() != null) {
            rippleColor = ColorUtils.setAlphaComponent(palette.getVibrantSwatch().getRgb(), darkAlpha);
        } else if (palette.getLightVibrantSwatch() != null) {
            rippleColor = ColorUtils.setAlphaComponent(palette.getLightVibrantSwatch().getRgb(),
                    lightAlpha);
        } else if (palette.getDarkVibrantSwatch() != null) {
            rippleColor = ColorUtils.setAlphaComponent(palette.getDarkVibrantSwatch().getRgb(),
                    darkAlpha);
        } else if (palette.getMutedSwatch() != null) {
            rippleColor = ColorUtils.setAlphaComponent(palette.getMutedSwatch().getRgb(), darkAlpha);
        } else if (palette.getLightMutedSwatch() != null) {
            rippleColor = ColorUtils.setAlphaComponent(palette.getLightMutedSwatch().getRgb(),
                    lightAlpha);
        } else if (palette.getDarkMutedSwatch() != null) {
            rippleColor =
                    ColorUtils.setAlphaComponent(palette.getDarkMutedSwatch().getRgb(), darkAlpha);
        }
        return new RippleDrawable(ColorStateList.valueOf(rippleColor), null,
                bounded ? new ColorDrawable(Color.WHITE) : null);
    }

    /**
     * Checks if the given swatch is dark
     * <p/>
     * Annoyingly we have to return this Lightness 'enum' rather than a boolean as palette isn't
     * guaranteed to find the most populous color.
     */
    public static boolean isDark(Palette.Swatch swatch) {
        return isDark(swatch.getHsl());
    }

    /**
     * Check that the lightness value (0–1)
     */
    public static boolean isDark(float[] hsl) { // @Size(3)
        return hsl[2] < 0.5f;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
