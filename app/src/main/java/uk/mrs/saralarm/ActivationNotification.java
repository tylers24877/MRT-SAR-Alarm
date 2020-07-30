package uk.mrs.saralarm;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class ActivationNotification {
    public static String NOTIFICATION_ACTION_SAR_A = "uk.mrs.saralarm.notification_sar_a";

    static void notify(final Context context, String messageBody) {

        final Resources res = context.getResources();

        //The title of the Notification.
        final String title = res.getString(R.string.acitvation_notification_title_template);


        //fullscreen intent
        Intent fullScreenIntent = new Intent(context, Alarm.class);
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //action notification
        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.setAction(NOTIFICATION_ACTION_SAR_A);
        PendingIntent actionIntent = PendingIntent.getBroadcast(context,0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(0, "SAR A", actionIntent).build();

        //build notification
        String channelId = "Alarm triggered";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.notif_icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setColor(Color.argb(255,204,51,1))
                        .setFullScreenIntent(fullScreenPendingIntent, true)
                        .addAction(action);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Alarm Trigger",
                    NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        //notify notification (display)
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @SuppressLint("UnlocalizedSms")
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (NOTIFICATION_ACTION_SAR_A.equals(intent.getAction())) {
                //send SMS to SARCALL
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("07537415551", null, "SAR A", null, null);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(0);

                Toast toast = Toast.makeText(context, "SAR A sent to SARCALL", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}