package gachon.mpclass.prezzy_pop.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import gachon.mpclass.prezzy_pop.MainActivity;
import gachon.mpclass.prezzy_pop.R;

public class ScreenService extends Service {

    private ScreenBroadCast currentReceiver; // 현재 리시버
    private Intent currentIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Start Checking", Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "Screen Monitoring";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Screen Monitoring",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);


            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Intent notificationIntent=new Intent(this,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Prezzy Pop")
                    .setContentText("Time Checking...")
                    .setSmallIcon(R.drawable.ballon)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(2, notification);
        }

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        try {
            currentIntent=intent;
            String getState = intent.getExtras().getString("state");
            assert getState != null;
            switch (getState) {
                case "on":
                    startId = 1;
                    break;
                case "off":
                    startId = 0;
                    stopForeground(true);
                    break;
                default:
                    startId = 0;
                    break;
            }
        } catch (NullPointerException e) {
            startId=0;
        }

        if (startId == 1) {
            final ScreenBroadCast receiver = new ScreenBroadCast();
            IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
            filter.addAction("android.intent.action.SCREEN_OFF");
            registerReceiver(receiver, filter);
            setReceiver(receiver);
        }



        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this, "Stop Checking", Toast.LENGTH_LONG).show();
        int time=this.currentReceiver.getTime();
        printTime(time,this);

        //Intent timeIntent=new Intent(getApplicationContext(), MainActivity.class);
        //timeIntent.putExtra("time",time);
        //startActivity(timeIntent);
    }

    public void setReceiver(ScreenBroadCast rcv){
        this.currentReceiver=rcv;
    }

    public void printTime(int sec,Context context){
        int minutes = sec/60;
        int hours = minutes/60;
        String msg="Good job!\nYou save ";
        if (hours > 0) {
            sec = sec%60;
            minutes = minutes%60;
            Toast.makeText(context, msg+hours+"hour "+minutes+"min "+sec+"sec", Toast.LENGTH_LONG).show();
        }
        else if (minutes > 0) {
            sec = sec%60;
            Toast.makeText(context, msg+minutes+"min "+sec+"sec", Toast.LENGTH_LONG).show();
        }
        else {
            if (sec!=0)
                Toast.makeText(context, msg+sec + "sec", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, msg+"0 sec", Toast.LENGTH_LONG).show();
        }
    }


}