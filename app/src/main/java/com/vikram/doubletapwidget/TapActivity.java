package com.vikram.doubletapwidget;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

public final class TapActivity extends Activity {
    private static final String PREFS = "tap_state";
    private static final String LAST_TAP = "last_tap";
    private static final long DOUBLE_TAP_TIMEOUT_MS = 450L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long now = SystemClock.elapsedRealtime();
        long previous = getSharedPreferences(PREFS, MODE_PRIVATE).getLong(LAST_TAP, 0L);

        if (previous > 0L && now - previous <= DOUBLE_TAP_TIMEOUT_MS) {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putLong(LAST_TAP, 0L).apply();
            if (!LockAccessibilityService.lockScreen()) {
                Toast.makeText(this, "Enable the Double Tap Lock Service first", Toast.LENGTH_SHORT).show();
            }
        } else {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putLong(LAST_TAP, now).apply();
        }

        finish();
        overridePendingTransition(0, 0);
    }
}
