package com.github.clans.daviart;

import android.content.Intent;

public class Intents {

    public static final String ACTION_SYS_WINDOW_INSETS = "com.github.clans.daviart.ACTION_SYS_WINDOW_INSETS";

    public static final String EXTRA_SYS_WINDOW_INSET_TOP = "com.github.clans.daviart.EXTRA_SYS_WINDOW_INSET_TOP";
    public static final String EXTRA_SYS_WINDOW_INSET_BOTTOM = "com.github.clans.daviart.EXTRA_SYS_WINDOW_INSET_BOTTOM";

    private Intents() {
    }

    public static Intent getSysWindowInsetsBroadcastIntent(int insetTop, int insetBottom) {
        Intent i = new Intent(ACTION_SYS_WINDOW_INSETS);
        i.putExtra(EXTRA_SYS_WINDOW_INSET_TOP, insetTop);
        i.putExtra(EXTRA_SYS_WINDOW_INSET_BOTTOM, insetBottom);
        return i;
    }
}
