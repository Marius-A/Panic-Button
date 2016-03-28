package ro.go.mariusiliescu.panicbutton.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import ro.go.mariusiliescu.panicbutton.constants.KeyConstant;
import ro.go.mariusiliescu.panicbutton.utils.GpsUtils;
import ro.go.mariusiliescu.panicbutton.utils.PhoneData;
import ro.go.mariusiliescu.panicbutton.utils.SmsSender;
import ro.go.mariusiliescu.panicbutton.utils.TxtManager;

public class MediaButtonIntentReciever extends BroadcastReceiver
{
    private static long prevTime;

    private static long msgSendTime;

    private static PowerManager pm;

    private static boolean isSingleCall = false;

    private static int counter = 0;
    private static boolean msgSend = false;


    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    public MediaButtonIntentReciever()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        GpsUtils gps;
        SmsSender sms;
        TxtManager txtManager;

        if ( intent != null && intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {

            Log.d("x","media button action recieved");

            if (intent.getExtras() != null) {
                Log.d("x","intend!= null");
                if (pm == null) {
                    pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

                    Log.d("x","power manager is initialized");
                }

                int prevVolume = intent.getExtras().getInt("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);
                int currentValue = intent.getExtras().getInt("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);

                AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                //Forunlocking only one event will trigger. When phone is open ui popup will also trigger the volume change
                boolean volumeAction = false;
                if ((currentValue == 0 && prevVolume == 0) || currentValue == maxVolume && prevVolume == maxVolume) {
                    if (!isSingleCall) {
                        isSingleCall = true; // Wait for ui volume change call
                    } else {
                        isSingleCall = false;   //Resetting value
                        volumeAction = true;
                    }
                } else if (currentValue != 0 && prevVolume != 0 && currentValue != prevVolume) {
                    volumeAction = true;
                }

                if (volumeAction || !isScreenOn(pm)) //when screen is off ui volume change will not happen
                {
                   // boolean isEnabled = PhoneData.getPhoneData(context, KeyConstant.UNLOCK_STR, false);

                    if (!isScreenOn(pm) && PhoneData.getPhoneData(context, KeyConstant.VOLUME_LOCK_ENABLE_STR, false)) {

                        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK , KeyConstant.WAKE_TAG_STR);
                        wakeLock.acquire();
                        wakeLock.release();


                        if (Math.abs(System.currentTimeMillis() - prevTime) < 2000) {
                            counter++;
                        } else {
                            counter = 0;
                        }

                        Log.d("x", "counter = " + counter);
                        Log.d("x", "msgSend = " + msgSend);

                        if (counter >= 4 && !msgSend) {
                            msgSendTime = System.currentTimeMillis();
                            msgSend = true;

                            gps = new GpsUtils(context);
                            sms = new SmsSender(context);
                            txtManager = new TxtManager(context);
                            String phoneNumber = txtManager.readFromFile();

                            Log.d("x", "latitude  = " + gps.getLatitude());
                            Log.d("x", "longitude = " + gps.getLongitude());
                            Log.d("x", "Sms sended to : " + phoneNumber);

                            sms.sendSms(phoneNumber, "Ajutor!\n Arabii !!!\n https://www.google.ro/maps/place/" + gps.getLatitude() + "," + gps.getLongitude() + "");

                            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(300);
                            counter = 0;
                        }

                        prevTime = System.currentTimeMillis();

                        long i = prevTime - msgSendTime;
                        Log.d("x", "prevTime -msgSendTime = " + i);

                        if ((prevTime - msgSendTime) > 5000) {
                            msgSend = false;
                        }

                    }
                }

            }
        }
    }


    @SuppressWarnings("deprecation")
    private boolean isScreenOn(PowerManager pm)
    {
        boolean isScreenOn;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH)
        {
            isScreenOn = pm.isInteractive();
        }
        else
        {
            isScreenOn = pm.isScreenOn();
        }

        return isScreenOn;
    }

}
