package com.developerdru.vividity.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.developerdru.vividity.R;
import com.developerdru.vividity.screens.home.HomeScreen;

/**
 * Implementation of App Widget functionality.
 */
public class PhotoListWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.photo_list_widget);

        // Setting remote adapter
        Intent PhotoListServiceIntent = new Intent(context, PhotoListService.class);
        PhotoListServiceIntent.putExtra(PhotoListService.EXTRA_WIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.stack_widget, PhotoListServiceIntent);
        views.setEmptyView(R.id.stack_widget, R.id.tvEmptyWidget);

        // Setting pending intent template for stackview
        Intent stackItemClickIntent = new Intent(context, HomeScreen.class);
        stackItemClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent stackItemPI = PendingIntent.getActivity(context,
                0, stackItemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.stack_widget, stackItemPI);

        // launch app when empty widget is clicked
        Intent appOpenIntent = new Intent(context, HomeScreen.class);
        PendingIntent appOpenPI = PendingIntent.getActivity(context,
                0, appOpenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tvEmptyWidget, appOpenPI);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

