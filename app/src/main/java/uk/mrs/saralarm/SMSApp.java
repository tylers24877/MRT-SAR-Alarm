/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 28/07/20 13:26
 *
 */

package uk.mrs.saralarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.view.Display;

import androidx.preference.PreferenceManager;

import java.util.Set;

/**
 * The BroadcastReceiver of the Text messages.
 *
 * Created by Tyler on 25/03/2015.
 * (c)2014 Tyler Simmonds||All rights reserved.
 */
public class SMSApp extends BroadcastReceiver {
    /**
     * Called when a text message is received.
     *
     * @param context Reference to the application context.
     * @param intent  Intent of the text message?
     */

    public void onReceive(final Context context, Intent intent) {
        //get preferences for application
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        //check weather the app is enabled.
        if (pref.getBoolean("prefEnabled", false)) {
            Set<String> activationSet = pref.getStringSet("triggerResponses", null);
            if (activationSet == null || activationSet.isEmpty()) return;

            //get bundle of extras from intent.
            Bundle bundle = intent.getExtras();
            //check whether the bundle is not null
            if (bundle != null) {
                //if true, get the string of the text message content.
                final Object[] plusObj = (Object[]) bundle.get("pdus");

                assert plusObj != null;
                for (Object aPlusObj : plusObj) {

                    //get the current message from Pdu.
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPlusObj);

                    //work out the body message from the current SMS message
                    String message = currentMessage.getDisplayMessageBody();

                    //if true,
                    //create a notification alerting that the alarm has sounded.
                    if (checkStringSet(activationSet, message))
                        if (checkScreenState(context)) {
                            ActivationNotification.notify_postAlarm(context);
                        } else {
                            ActivationNotification.notify(context);
                        }
                }
            }
        }
    }

    /**
     * @param SS Set - Set of Strings.
     * @param m  KeyString - Check against the set(SS) to see it it starts with m
     * @return Whether there was a match.
     */
    public boolean checkStringSet(Set<String> SS, String m) {
        for (String s : SS) {
            if (m.toLowerCase().replaceAll("\\s+", "").startsWith(s.replaceAll("\\s+", "").toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param context App context.
     * @return True if screen is ON. False is screen is OFF
     */
    private boolean checkScreenState(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            for (Display display : dm.getDisplays()) {
                if (display.getState() == Display.STATE_ON) {
                    return true;
                }
            }
            return false;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //noinspection
            return pm.isScreenOn();
        }
    }
}