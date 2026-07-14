package com.vikram.doubletapwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public final class DoubleTapWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) updateWidget(context, manager, appWidgetId);
    }

    public static void refreshAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, DoubleTapWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(provider);
        for (int id : ids) updateWidget(context, manager, id);
    }

    private static void updateWidget(Context context, AppWidgetManager manager, int appWidgetId) {
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
        boolean visible = Preferences.showWidgetArea(context);
        views.setInt(R.id.widgetRoot, "setBackgroundResource",
                visible ? R.drawable.widget_debug_background : android.R.color.transparent);
        views.setTextViewText(R.id.tapTarget, visible ? context.getString(R.string.widget_area_label) : "");
        manager.updateAppWidget(appWidgetId, views);
    }
}
