package gachon.mpclass.prezzy_pop;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class SetBalloon {
    public static void setCurrentBalloon(String child_key, BalloonStat balloonStat) {
        DatabaseReference newBalloonRef = DB_Reference.balloonRef.child(child_key).push();
        DatabaseReference childRef = DB_Reference.childRef.child(child_key);
        DatabaseReference childCurBalloonRef = childRef.child("current_balloon_id");

        String newBalloonAddress = newBalloonRef.getKey();

        newBalloonRef.setValue(balloonStat);        //새 풍선 입력
        childCurBalloonRef.setValue(newBalloonAddress);    //새 풍선 주소 입력
    }

    public static void deleteBalloon(String child_key, String balloonID) {
        DatabaseReference childCurBalloonRef = DB_Reference.childRef.child(child_key).child("current_balloon_id");
        DatabaseReference childBalloonRef = DB_Reference.balloonRef.child(child_key).child(balloonID);

        childCurBalloonRef.setValue(null);
        childBalloonRef.setValue(null);
    }
}