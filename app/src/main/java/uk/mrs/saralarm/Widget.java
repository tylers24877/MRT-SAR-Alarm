/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 28/07/20 12:48
 *
 */

package uk.mrs.saralarm;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {

    public static String WIDGET_CLICK = "widgetclick";


    private PendingIntent getPendingSelfIntent(Context context, String action) {
        // An explicit intent directed at the current class (the "self").
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, RemoteViews views) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        CharSequence widgetText = context.getString(R.string.appwidget_text);

        if(pref.getBoolean("prefEnabled",false))
            views.setTextColor(R.id.button, Color.GREEN);
        else
            views.setTextColor(R.id.button, Color.RED);

        views.setTextViewText(R.id.button, widgetText);

        // Instruct the app_widget manager to update the app_widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Loop for every App Widget instance that belongs to this provider.
        // Noting, that is, a user might have multiple instances of the same
        // widget on
        // their home screen.
        for (int appWidgetID : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.app_widget);

            remoteViews.setOnClickPendingIntent(R.id.button, getPendingSelfIntent(context, WIDGET_CLICK));

            updateAppWidget(context,appWidgetManager, appWidgetID, remoteViews);
        }
    }

    private void onUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);

        // Uses getClass().getName() rather than MyWidget.class.getName() for
        // portability into any App Widget Provider Class
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(),getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (WIDGET_CLICK.equals(intent.getAction())) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            if(pref.getBoolean("prefEnabled",false)) {
                pref.edit().putBoolean("prefEnabled", false).apply();
                Toast toast = Toast.makeText(context, "SARCALL alarm disabled", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                pref.edit().putBoolean("prefEnabled", true).apply();
                Toast toast = Toast.makeText(context, "SARCALL alarm enabled", Toast.LENGTH_SHORT);
                toast.show();
            }
            onUpdate(context);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first app_widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last app_widget is disabled
    }
}

