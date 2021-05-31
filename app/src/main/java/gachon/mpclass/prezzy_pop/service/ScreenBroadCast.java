package gachon.mpclass.prezzy_pop.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import gachon.mpclass.prezzy_pop.DB_Reference;
import gachon.mpclass.prezzy_pop.TimeAlgorithm;

public class ScreenBroadCast extends BroadcastReceiver {
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String email = user.getEmail();
    String key = email.split("@")[0];

    private int time = 0;


    ScreenBroadCast (int time) {
        this.time = time;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Date date = new Date();
            SharedPreferences pref = context.getSharedPreferences("Time", Context.MODE_PRIVATE);
            Long onTime = date.getTime();
            Long offTime = pref.getLong("offTime", onTime);
            long diff = onTime - offTime;
            long sec = diff / 1000;
            int score = new TimeAlgorithm(sec).getPenaltyTime();
            time+=score;

            printTime(score,context);

            DatabaseReference childRef = DB_Reference.childRef.child(key).child("current_balloon_id");

            childRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    String balloonAdr = task.getResult().getValue(String.class);
                    if (balloonAdr != null) {
                        DB_Reference.balloonRef.child(key).child(balloonAdr).child("cur_time").setValue(time);
                    }
                    else {
                        Log.e("ScreenBroadCast", "get cur_time error from balloon");
                    }
                }
            });
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
        String msg;
        if (hours > 0) {
            msg="우와! 대단해요\n";
            sec = sec%60;
            minutes = minutes%60;
            Toast.makeText(context, msg+hours+"시간 "+minutes+"분 "+sec+"초 집중했어요", Toast.LENGTH_LONG).show();
        }
        else if (minutes > 0) {
            msg="잘했어요!\n";
            sec = sec%60;
            Toast.makeText(context, msg+minutes+"분 "+sec+"초 집중했어요", Toast.LENGTH_LONG).show();
        }
        else {
            msg="좀 더 노력해봐요\n";
            if (sec!=0)
                Toast.makeText(context, msg+sec + "초 집중했어요", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, msg+"집중을 안했어요!", Toast.LENGTH_LONG).show();
        }
    }

    public int getTime(){
        return time;
    }
}