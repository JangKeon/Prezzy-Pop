package gachon.mpclass.prezzy_pop.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.Iterator;

import gachon.mpclass.prezzy_pop.DB_Reference;

public class ScreenBroadCast extends BroadcastReceiver {
    private int time=0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Date date = new Date();
            SharedPreferences pref = context.getSharedPreferences("Time", Context.MODE_PRIVATE);
            Long onTime = date.getTime();
            Long offTime = pref.getLong("offTime", onTime);
            long diff = onTime - offTime;
            long sec = diff / 1000;
            time+=sec;
            printTime(sec,context);
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Date date = new Date();
            Long offTime = date.getTime();
            SharedPreferences pref = context.getSharedPreferences("Time", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =pref.edit();
            editor.putLong("offTime", offTime);
            editor.apply();
        }
    }

    public void printTime(long sec,Context context){
        long minutes = sec/60;
        long hours = minutes/60;
        String msg="Recent save time: ";
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
        Toast.makeText(context, "Cumulative save time : "+time+"sec", Toast.LENGTH_LONG).show();
    }

    public int getTime(){
        return time;
    }

}