package gachon.mpclass.prezzy_pop.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import gachon.mpclass.prezzy_pop.DB_Reference;
import gachon.mpclass.prezzy_pop.MainActivity;
import gachon.mpclass.prezzy_pop.R;

public class ScreenService extends Service {

    private ScreenBroadCast currentReceiver; // 현재 리시버

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
            start_checking(FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0]);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this, "Stop Checking", Toast.LENGTH_LONG).show();
        int time=this.currentReceiver.getTime();
        printTime(time,this);
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

    public void start_checking(String key) {
        DatabaseReference childRef = DB_Reference.childRef.child(key).child("Current_Balloon");

        childRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String balloonAdr = task.getResult().getValue(String.class);

                DatabaseReference balloonRef = DB_Reference.balloonRef.child(key).child(balloonAdr).child("cur_time");

                balloonRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("", "Error getting data", task.getException());
                        } else {
                            int cur_time = task.getResult().getValue(Integer.TYPE);
                            Log.d("doowon", "init time : " + cur_time);

                            final ScreenBroadCast receiver = new ScreenBroadCast(cur_time);
                            IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
                            filter.addAction("android.intent.action.SCREEN_OFF");
                            registerReceiver(receiver, filter);
                            setReceiver(receiver);
                        }
                    }
                });
            }
        });
    }
}