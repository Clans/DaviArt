package com.github.clans.daviart.util;

import android.annotation.TargetApi;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;

public class RippleApplier {

    private RippleApplier() { }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setRipple(Palette.Swatch swatch, CardView cardView) {
        RippleDrawable ripple = Utils.createRipple(swatch, 0x66, 0x80, true);
        cardView.setForeground(ripple);
    }
}
