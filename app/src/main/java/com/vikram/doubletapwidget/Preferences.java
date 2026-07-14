package com.vikram.doubletapwidget;

import android.content.Context;
import android.content.SharedPreferences;

public final class Preferences {
    private static final String NAME = "gesture_settings";
    private static final String KEY_INTERVAL = "tap_interval";
    private static final String KEY_DOUBLE_DELAY = "double_delay";
    private static final String KEY_TRIPLE_ENABLED = "triple_enabled";
    private static final String KEY_HAPTIC = "haptic";
    private static final String KEY_FOUR_ACTION = "four_action";

    private Preferences() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static int tapInterval(Context context) {
        return prefs(context).getInt(KEY_INTERVAL, 420);
    }

    public static void setTapInterval(Context context, int value) {
        prefs(context).edit().putInt(KEY_INTERVAL, value).apply();
    }

    public static int doubleActionDelay(Context context) {
        return prefs(context).getInt(KEY_DOUBLE_DELAY, 280);
    }

    public static void setDoubleActionDelay(Context context, int value) {
        prefs(context).edit().putInt(KEY_DOUBLE_DELAY, value).apply();
    }

    public static boolean tripleEnabled(Context context) {
        return prefs(context).getBoolean(KEY_TRIPLE_ENABLED, true);
    }

    public static void setTripleEnabled(Context context, boolean value) {
        prefs(context).edit().putBoolean(KEY_TRIPLE_ENABLED, value).apply();
    }

    public static boolean hapticEnabled(Context context) {
        return prefs(context).getBoolean(KEY_HAPTIC, true);
    }

    public static void setHapticEnabled(Context context, boolean value) {
        prefs(context).edit().putBoolean(KEY_HAPTIC, value).apply();
    }

    public static int fourTapAction(Context context) {
        return prefs(context).getInt(KEY_FOUR_ACTION, 0);
    }

    public static void setFourTapAction(Context context, int value) {
        prefs(context).edit().putInt(KEY_FOUR_ACTION, value).apply();
    }
}
