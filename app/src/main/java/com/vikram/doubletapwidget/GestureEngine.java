package com.vikram.doubletapwidget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

/**
 * Small deterministic state machine for click-only widgets.
 * Every new tap restarts one decision timer. When the sequence settles,
 * exactly one configured action is executed.
 */
public final class GestureEngine {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static long lastTapAt;
    private static int tapCount;
    private static Runnable decision;

    private GestureEngine() {}

    public static synchronized void registerTap(Context context) {
        Context app = context.getApplicationContext();
        long now = SystemClock.elapsedRealtime();
        int maxGap = Preferences.tapInterval(app);

        if (lastTapAt == 0L || now - lastTapAt > maxGap) {
            clearPending();
            tapCount = 1;
        } else {
            tapCount = Math.min(4, tapCount + 1);
        }
        lastTapAt = now;

        clearDecisionOnly();
        int capturedCount = tapCount;
        decision = () -> settle(app, capturedCount);
        HANDLER.postDelayed(decision, Math.max(Preferences.actionDelay(app), maxGap));
    }

    private static synchronized void settle(Context context, int capturedCount) {
        if (capturedCount != tapCount) return;

        int action;
        if (capturedCount >= 4) {
            action = Preferences.fourTapAction(context);
        } else if (capturedCount == 3) {
            action = Preferences.tripleAction(context);
        } else if (capturedCount == 2) {
            action = Preferences.doubleAction(context);
        } else {
            action = Preferences.ACTION_DISABLED;
        }

        reset();
        ActionExecutor.execute(context, action);
    }

    private static void clearDecisionOnly() {
        if (decision != null) HANDLER.removeCallbacks(decision);
        decision = null;
    }

    private static void clearPending() {
        clearDecisionOnly();
        lastTapAt = 0L;
        tapCount = 0;
    }

    private static synchronized void reset() {
        clearPending();
    }
}
