package com.vikram.doubletapwidget;

import android.content.Context;
import android.content.SharedPreferences;

public final class Preferences {
    private static final String NAME = "gesture_settings";

    public static final int ACTION_DISABLED = 0;
    public static final int ACTION_LOCK = 1;
    public static final int ACTION_TORCH = 2;
    public static final int ACTION_RECENTS = 3;
    public static final int ACTION_NOTIFICATIONS = 4;
    public static final int ACTION_QUICK_SETTINGS = 5;

    private static final String KEY_INTERVAL = "tap_interval";
    private static final String KEY_ACTION_DELAY = "action_delay";
    private static final String KEY_DOUBLE_ACTION = "double_action";
    private static final String KEY_TRIPLE_ACTION = "triple_action";
    private static final String KEY_FOUR_ACTION = "four_action_v2";
    private static final String KEY_HAPTIC = "haptic";
    private static final String KEY_HAPTIC_DURATION = "haptic_duration";
    private static final String KEY_SHOW_WIDGET_AREA = "show_widget_area";
    private static final String KEY_ACTION_COUNT = "action_count";

    private Preferences() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static int tapInterval(Context context) {
        return prefs(context).getInt(KEY_INTERVAL, 420);
    }

    public static void setTapInterval(Context context, int value) {
        prefs(context).edit().putInt(KEY_INTERVAL, clamp(value, 200, 800)).apply();
    }

    public static int actionDelay(Context context) {
        return prefs(context).getInt(KEY_ACTION_DELAY, 300);
    }

    public static void setActionDelay(Context context, int value) {
        prefs(context).edit().putInt(KEY_ACTION_DELAY, clamp(value, 100, 700)).apply();
    }

    public static int doubleAction(Context context) {
        return prefs(context).getInt(KEY_DOUBLE_ACTION, ACTION_LOCK);
    }

    public static void setDoubleAction(Context context, int action) {
        prefs(context).edit().putInt(KEY_DOUBLE_ACTION, action).apply();
    }

    public static int tripleAction(Context context) {
        return prefs(context).getInt(KEY_TRIPLE_ACTION, ACTION_TORCH);
    }

    public static void setTripleAction(Context context, int action) {
        prefs(context).edit().putInt(KEY_TRIPLE_ACTION, action).apply();
    }

    public static int fourTapAction(Context context) {
        return prefs(context).getInt(KEY_FOUR_ACTION, ACTION_DISABLED);
    }

    public static void setFourTapAction(Context context, int action) {
        prefs(context).edit().putInt(KEY_FOUR_ACTION, action).apply();
    }

    public static boolean hapticEnabled(Context context) {
        return prefs(context).getBoolean(KEY_HAPTIC, true);
    }

    public static void setHapticEnabled(Context context, boolean value) {
        prefs(context).edit().putBoolean(KEY_HAPTIC, value).apply();
    }

    public static int hapticDuration(Context context) {
        return prefs(context).getInt(KEY_HAPTIC_DURATION, 35);
    }

    public static void setHapticDuration(Context context, int value) {
        prefs(context).edit().putInt(KEY_HAPTIC_DURATION, clamp(value, 10, 100)).apply();
    }

    public static boolean showWidgetArea(Context context) {
        return prefs(context).getBoolean(KEY_SHOW_WIDGET_AREA, false);
    }

    public static void setShowWidgetArea(Context context, boolean value) {
        prefs(context).edit().putBoolean(KEY_SHOW_WIDGET_AREA, value).apply();
    }

    public static int actionCount(Context context) {
        return prefs(context).getInt(KEY_ACTION_COUNT, 0);
    }

    public static void incrementActionCount(Context context) {
        SharedPreferences p = prefs(context);
        p.edit().putInt(KEY_ACTION_COUNT, p.getInt(KEY_ACTION_COUNT, 0) + 1).apply();
    }

    public static void resetDefaults(Context context) {
        prefs(context).edit().clear().apply();
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
