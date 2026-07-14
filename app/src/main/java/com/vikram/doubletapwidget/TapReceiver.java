package com.vikram.doubletapwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class TapReceiver extends BroadcastReceiver {
    public static final String ACTION_WIDGET_TAP = "com.vikram.doubletapwidget.WIDGET_TAP";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && ACTION_WIDGET_TAP.equals(intent.getAction())) {
            GestureEngine.registerTap(context);
        }
    }
}
