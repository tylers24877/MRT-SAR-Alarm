package uk.mrs.saralarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.preference.PreferenceManager;


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

                    //check whether the message, when lower case and no spaces, equals the string
                    // entered in preferences, when lower case and all spaces removed.
                    String[] activation = {"LEEMINGMRT FT", "VALLEYMRT FT", "LOSSIEMRT FT"};

                    //if true,
                    //create a notification alerting that the alarm has sounded.
                    if (checkStringArray(activation, message))
                        ActivationNotification.notify(context, context.getString(R.string.notifcation_default_message));
                }
            }
        }
    }

    public boolean checkStringArray(String[] SA, String m) {
        for (String s : SA) {
            if(m.toLowerCase().replaceAll("\\s+", "").startsWith(s.replaceAll("\\s+", "").toLowerCase())){
                return true;
            }
        }
        return false;
    }
}