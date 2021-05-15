package gachon.mpclass.prezzy_pop;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

public class SetBalloon {
    public static void setCurrentBalloon(String child_key, BalloonStat balloonStat, boolean overwrite) {
        DatabaseReference newBalloonRef = DB_Reference.balloonRef.child(child_key).push();
        DatabaseReference childRef = DB_Reference.childRef.child(child_key);
        DatabaseReference childCurRef = childRef.child("Current_Balloon");

        String newBalloonAddress = newBalloonRef.getKey();

        if (!overwrite) {   //current를 history로 옮기기
            childCurRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {       //child data 읽어오기
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("doowon", "Error getting data", task.getException());
                    } else {
                        String current_balloon_name = task.getResult().getValue(String.class);

                        if (current_balloon_name != null) {     //Current_Balloon이 안 비어있을 때 : 현재 풍선 주소를 History로 옮기고 새로운 풍선 세팅  
                            curToHistory(child_key, current_balloon_name);

                            childCurRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    newBalloonRef.setValue(balloonStat);
                                    childCurRef.setValue(newBalloonAddress);
                                }
                            });
                        }
                        else {  //Current_Balloon이 비어있을 때 : 새로운 풍선 세팅 
                            newBalloonRef.setValue(balloonStat);
                            childCurRef.setValue(newBalloonAddress);
                        }
                    }
                }
            });
        }
        else {  //덮어쓰기
            childCurRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {    //Current_Balloon 주소 가져오기
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    String oldBalloonAdr = task.getResult().getValue(String.class);
                    Log.e("doowon", "old balloon : " + oldBalloonAdr);

                    if (oldBalloonAdr != null) {
                        DatabaseReference oldBalloonRef = DB_Reference.balloonRef.child(child_key).child(oldBalloonAdr);
                        oldBalloonRef.removeValue();                //old 풍선 지우기
                    }
                }
            });
            newBalloonRef.setValue(balloonStat);        //새 풍선 입력
            childCurRef.setValue(newBalloonAddress);    //새 풍선 주소 입력
        }
    }

    public static void curToHistory(String child_key, String current_balloon_name) {
        DatabaseReference childRef = DB_Reference.childRef.child(child_key);
        DatabaseReference childHistoryRef = childRef.child("History");

        childHistoryRef.push().setValue(current_balloon_name);
    }
}




//curRef.child("child_list").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//@Override
//public void onComplete(@NonNull Task<DataSnapshot> task) {
//        ArrayList<String> child_list = new ArrayList<>();
//
//        for(DataSnapshot child_key : task.getResult().getChildren()) {
//        child_list.add(child_key.getValue(String.class));
//        }
//
//        if (child_list.size() == 1) {
//        SetBalloon.setCurrentBalloon(child_list.get(0), newBalloon, true);
//        }
//        else if(child_list.size() == 0) {
//        Log.e("doowon", "has no child! (can't make balloon)");
//        }
//        else {
////                                for (String s : child_list) {} //다음값이 있는지 체크
//        Log.e("doowon", "has 2 or more children\nchild_list" + child_list.toString());
//        }
//        }
//        });

