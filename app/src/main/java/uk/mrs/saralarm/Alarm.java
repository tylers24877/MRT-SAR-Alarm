package uk.mrs.saralarm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.io.IOException;


/**
 * This is the Java class that controls the Alarm when triggered.
 * This gets triggered from {@link SMSApp}
 */
public class Alarm extends Activity {
    //The instance of the Media Player
    MediaPlayer mp;

    //The instance of the Wake lock so the alarm will work on a locked device.
    //private PowerManager.WakeLock wl;

    //Instance of the vibration controller.
    public Vibrator vibrator;

    //The original audio volume before the siren is played.
    private int originalAudio;

    /**
     * Called when the activity is created.
     * @param savedInstanceState The saved state of an application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //----------------------------------------------------------------
        //set the activity to be full screen and lockscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //----------------------------------------------------------------

        //set the view of the activity to the xml file.
        setContentView(R.layout.alarm);

        //create a new media player
        mp = new MediaPlayer();

        //set the steam type of the media player. this is so it plays at full volume even when muted
        // and headphones plugged in.
        mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

        //make the sound file loop so it will never stop.
        mp.setLooping(true);

        try {
            //try and load the sound file.
            //mp.setDataSource(getApplicationContext(), ResourceToUri(getApplication(), R.raw.wail));
            mp.setDataSource(getApplicationContext(),RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE));


            //this prepares the player for playback.
            mp.prepare();
        } catch (IOException e) {
            //print to stack trace if an IOException happens
            e.printStackTrace();
        }

        //start the playback of the media player.
        mp.start();

        //force the sound to be full volume.
        //get the audio manager for the device.
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //get the stream max volume it can support.
        assert audio != null;
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);

        //set originalAudio to the current volume of the stream used.
        originalAudio = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

        //now set the volume to max.
        audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume, 0);

        //set the mode to pretend we are in a call.
        audio.setMode(AudioManager.MODE_IN_CALL);

        //set the speaker phone on so it ignores headphone's
        audio.setSpeakerphoneOn(true);
        //--------------
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        decorView.setSystemUiVisibility(uiOptions);
        //----------
        //now create the vibrations

        long[] pattern = {0, 200, 500};
        // get the vibrator service for the device.
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //start the vibrations.
        assert vibrator != null;
        vibrator.vibrate(pattern, 0);


        //Create a flashing background on the layout.
        //get the layout
        final RelativeLayout layout = findViewById(R.id.alarm_background);

        //create an animation drawable.
        final AnimationDrawable drawable = new AnimationDrawable();

        //create a new handler.
        final Handler handler = new Handler();

        //add different coloured frames for the background with 500 mills interval.
        drawable.addFrame(new ColorDrawable(Color.RED), 500);
        drawable.addFrame(new ColorDrawable(Color.GREEN), 500);

        //weather to play once or repeat.
        drawable.setOneShot(false);

        //set the background to the drawable.
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
            layout.setBackgroundDrawable(drawable);
        else
            layout.setBackground(drawable);

        //start the drawable animation.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.start();
            }
        }, 100);


        //If no user action within  1 min 30 sec, stop the alarm.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 90000); //1min 30 sec

        //set the button listener.
        findViewById(R.id.alarm_stop_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * Called when activity resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mp != null)
            //start playing the media player.
             mp.start();
    }

    /**
     * Called when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null)
            //pause the media player.
            mp.pause();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //get the audio manager
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //set the audio back the original.
        assert audio != null;
        audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, originalAudio, 0);
        vibrator.cancel();
    }
    @Override
    public void onBackPressed() {

    }
}