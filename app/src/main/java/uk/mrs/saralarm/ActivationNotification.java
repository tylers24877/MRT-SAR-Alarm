/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 08/08/20 00:41
 *
 */

package uk.mrs.saralarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;

import static androidx.core.app.NotificationCompat.BigTextStyle;
import static androidx.core.app.NotificationCompat.Builder;
import static androidx.core.app.NotificationCompat.CATEGORY_ALARM;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;


public class ActivationNotification {

    //Notification unique string
    public static String NOTIFICATION_ACTION_SAR_A = "uk.mrs.saralarm.notification_sar_a";


    /**
     * When called, the notification will appear, trigging the siren/alarm when the device is locked.
     *
     * @param context application context
     */
    static void notify(final Context context) {

        final Resources res = context.getResources();

        //The title of the Notification.
        final String title = res.getString(R.string.activation_notification_title_template);


        //fullscreen intent
        //Alarm.class is the activity for the siren and alarm.
        // TODO: 30/07/2020 Disable the alarm from triggering when clicking on the notification.
        Intent fullScreenIntent = new Intent(context, Alarm.class);
        fullScreenIntent
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //build notification
        String channelId = context.getString(R.string.notification_alarm_channel_id);
        Builder notificationBuilder =
                new Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setPriority(PRIORITY_MAX)
                        .setCategory(CATEGORY_ALARM)
                        .setColor(Color.argb(255, 204, 51, 1))
                        .setFullScreenIntent(fullScreenPendingIntent, true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo, notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        //notify notification (display)
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * When called, the notification will appear, trigging the siren/alarm when the device is locked.
     *
     * @param context application context
     */
    static void notify_postAlarm(final Context context) {

        final Resources res = context.getResources();

        //The title of the Notification.
        final String title = res.getString(R.string.activation_notification_title_template);

        //action notification
        //set the intent for the SAR A button. TODO: 30/07/2020  Add the other SAR button replies.


        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        //build notification
        String channelId = "Post Alarm Trigger";
        Builder notificationBuilder =
                new Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                        .setContentTitle(title)
                        .setContentText("SARCALL alarm triggered!")
                        .setStyle(new BigTextStyle().bigText("SARCALL alarm triggered! Click to respond."))
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setPriority(PRIORITY_MAX)
                        .setCategory(CATEGORY_ALARM)
                        .setColor(Color.argb(255, 204, 51, 1));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo, notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        //notify notification (display)
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
    }
}