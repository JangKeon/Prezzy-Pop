package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import gachon.mpclass.prezzy_pop.pushNoti.SendMessage;


public class Matching extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        Button button = findViewById(R.id.Matching);

        button.setOnClickListener(new View.OnClickListener(){       //클릭시 DB에서 email로 user 데이터를 받아와 showPopUp 메소드 실행
            @Override
            public void onClick(View v) {
                // child
                String email=((EditText)findViewById(R.id.Email_matching)).getText().toString();
                String key = email.split("@")[0];   //key에 @는 저장이 안되므로 앞에 ID만 분리
                DatabaseReference childRef = DB_Reference.childRef.child(key);

                childRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("", "Error getting data", task.getException());
                        } else {
                            Log.d("doowon", "In on click : " + task.getResult().getValue());
                            Child child = task.getResult().getValue(Child.class);
                            showPopUp(child);
                        }
                    }
                });
            }
        });
    }
    private void startActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }

    private void showPopUp(Child child) {
        AlertDialog.Builder ad = new AlertDialog.Builder(Matching.this);
        ad.setIcon(R.mipmap.ic_launcher);
        ad.setTitle("E-mail 확인");
        if (child != null) {
            String child_email = child.getKey() + "@" + child.getDomain();
            ad.setMessage(child_email + "\n\n닉네임 : " + child.getNick() + "(이)가 맞습니까?");
        }
        else {
            ad.setMessage("잘못된 E-mail 입니다.");
        }

        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeGroup(child);
                dialog.dismiss();
                FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();
                String parentEmail=cur_user.getEmail();
                String parentKey = parentEmail.split("@")[0];
                DatabaseReference parentRef = DB_Reference.parentRef.child(parentKey);

                parentRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("", "Error getting data", task.getException());
                        } else {

                            Parent parent = task.getResult().getValue(Parent.class);
                            //setParentNick(parent.getNick());
                            String parentNick=parent.getNick();
                            Log.d("doowon", "click2" );
                            Log.d("doowon", "click3"+parentNick);
                            SendMessage sendMessage = new SendMessage("매칭이 완료되었습니다","부모님 ("+parentNick+") 계정과 매칭되었습니다.");
                        }
                    }
                });



            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    private void makeGroup(Child child){
        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();

        if (cur_user != null) {
            String parent_email = cur_user.getEmail();
            String parent_key = parent_email.split("@")[0];   //key에 @는 저장이 안되므로 앞에 ID만 분리
            String child_key = child.getKey();

            DatabaseReference childRef = DB_Reference.childRef.child(child_key);
            DatabaseReference parentRef = DB_Reference.parentRef.child(parent_key);
            DatabaseReference balloonUserRef = DB_Reference.balloonRef.child(child_key);

            Date now = new Date();
            String strNow = DateString.DateToString(now);

            parentRef.child("child_list").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {       //child data 읽어오기
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("doowon", "Error getting data", task.getException());
                    } else {
                        if(!addChild(child, task)) {        //add child 실행 (parent)
                            Log.e("doowon", "이미 존재하는 child");
                            startToast("이미 매칭된 자녀입니다.");
                        }
                        else {                      //새로 매칭된 자녀이면
                            //기본 풍선 만들기(child)
//                            String child_key, int set_time, String achievement, String date, String parent_key, int cur_time, String state, String image
                            BalloonStat newBalloon = new BalloonStat(child_key, 600, "풍선을 만들어 보아요", strNow, parent_key, 300, "init", "");

                            SetBalloon.setCurrentBalloon(child_key, newBalloon);
                            Log.d("doowon", "click" );
                            startToast("매칭이 완료되었습니다");
                            startMyActivity(P_HomeActivity.class);
                        }
                    }
                }
            });

        } else {
            Log.e("doowon", "Failed make group");
        }
    }

    private boolean addChild(Child child, Task<DataSnapshot> task) {
        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();
        String child_key = child.getKey();
        String parent_email = cur_user.getEmail();
        String parent_key = parent_email.split("@")[0];   //key에 @는 저장이 안되므로 앞에 ID만 분리
        DatabaseReference parentRef = DB_Reference.parentRef.child(parent_key);

        for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
            if (dataSnapshot.getValue().equals(child_key)) {
                return false;
            }
        }
        parentRef.child("child_list").push().setValue(child_key);

        return true;
    }
    private void startMyActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}