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

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}

    @Override
    public void onDestroy() {
        instance.clear();
        super.onDestroy();
    }

    public static boolean lockScreen() {
        return performAction(GLOBAL_ACTION_LOCK_SCREEN);
    }

    public static boolean performAction(int action) {
        LockAccessibilityService service = instance.get();
        return service != null && service.performGlobalAction(action);
    }
}
