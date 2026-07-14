package com.vikram.doubletapwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public final class DoubleTapWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_double_tap);
            Intent tapIntent = new Intent(context, TapReceiver.class)
                    .setAction(TapReceiver.ACTION_WIDGET_TAP);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    tapIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.tapTarget, pendingIntent);
            manager.updateAppWidget(appWidgetId, views);
        }
    }
}
