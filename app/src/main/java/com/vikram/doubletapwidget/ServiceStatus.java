package com.vikram.doubletapwidget;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

public final class ServiceStatus {
    private ServiceStatus() {}

    public static boolean isAccessibilityEnabled(Context context) {
        String enabled = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (TextUtils.isEmpty(enabled)) return false;
        ComponentName expected = new ComponentName(context, LockAccessibilityService.class);
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        splitter.setString(enabled);
        while (splitter.hasNext()) {
            ComponentName actual = ComponentName.unflattenFromString(splitter.next());
            if (expected.equals(actual)) return true;
        }
        return false;
    }
}
