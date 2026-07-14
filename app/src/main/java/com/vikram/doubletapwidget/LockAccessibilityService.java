package com.vikram.doubletapwidget;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import java.lang.ref.WeakReference;

public final class LockAccessibilityService extends AccessibilityService {
    private static WeakReference<LockAccessibilityService> instance = new WeakReference<>(null);

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = new WeakReference<>(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // No global event processing. The service exists only to perform the lock action.
    }

    @Override
    public void onInterrupt() {
        // Nothing to interrupt.
    }

    @Override
    public void onDestroy() {
        instance.clear();
        super.onDestroy();
    }

    public static boolean lockScreen() {
        LockAccessibilityService service = instance.get();
        return service != null && service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
    }
}
