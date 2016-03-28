package ro.go.mariusiliescu.panicbutton.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import ro.go.mariusiliescu.panicbutton.constants.KeyConstant;
import ro.go.mariusiliescu.panicbutton.utils.PhoneData;
import ro.go.mariusiliescu.panicbutton.R;


public class ButtonCheckService extends Service
{
    private static ButtonCheckService buttonCheckService;
    private MediaPlayer mediaPlayer;

    public ButtonCheckService()
    {
        buttonCheckService = this;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("x","created");
        if(PhoneData.getPhoneData(this, KeyConstant.UNLOCK_STR, false))
        {
            if (mediaPlayer == null)
            {
                mediaPlayer = MediaPlayer.create(this, R.raw.sound);
                mediaPlayer.setVolume(0, 0);
                mediaPlayer.setLooping(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            if(!mediaPlayer.isPlaying())
            {
                mediaPlayer.start();
            }
        }
        else
        {
            stopMediaPlay();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy()
    {
        stopMediaPlay();

        super.onDestroy();
    }

    private void stopMediaPlay()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            Log.d("x","media stop");
        }
    }

    public static void manageService(Context context)
    {
        if (PhoneData.getPhoneData(context, KeyConstant.UNLOCK_STR, false))
        {
            Intent intent = new Intent(context, ButtonCheckService.class);
            context.startService(intent);
            Log.d("x","Service started");
        }
        else
        {
            stopService();
        }
    }

    private static void stopService() {
        try
        {   //safety
            if (buttonCheckService != null)
            {
                buttonCheckService.stopSelf();
            }
        } catch (Exception e) {
            Log.d("x",e.getMessage());
        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        Log.d("x", ServiceInfo.FLAG_STOP_WITH_TASK + "");
        Intent restartServiceIntent = new Intent(getApplicationContext(),
                this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}
