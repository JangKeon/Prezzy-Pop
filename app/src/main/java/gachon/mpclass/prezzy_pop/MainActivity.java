package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;


import gachon.mpclass.prezzy_pop.service.ScreenService;

public class MainActivity extends AppCompatActivity {
    DatabaseReference parentRef = DB_Reference.parentRef;
    DatabaseReference chidRef = DB_Reference.childRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_unmatched);

        // 로그인 된 상태가 아니라면
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startMyActivity(LoginActivity.class);
        }
        // 사용 버튼
        findViewById(R.id.button).setOnClickListener(onClickListener);
        findViewById(R.id.btn_logtout).setOnClickListener(onClickListener);
        findViewById(R.id.btn_present).setOnClickListener(onClickListener);
        findViewById(R.id.btn_time).setOnClickListener(onClickListener);
        findViewById(R.id.btn_startCheck).setOnClickListener(onClickListener);
        findViewById(R.id.btn_stopCheck).setOnClickListener(onClickListener);
        findViewById(R.id.btn_match).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_logtout:
                    FirebaseAuth.getInstance().signOut();
                    startToast("로그아웃되었습니다");
                    stopTimeCheck();
                    startMyActivity(LoginActivity.class);
                    break;
                case R.id.btn_present:
                    startMyActivity(PresentActivity.class);
                    break;
                case R.id.btn_time:
                    startMyActivity(TimeActivity.class);
                    break;
                case R.id.btn_startCheck:
                    startTimeCheck();
                    break;
                case R.id.btn_stopCheck:
                    stopTimeCheck();
                    break;
                case R.id.btn_match:
                    startMyActivity(Matching.class);
                    break;
                case R.id.button:   //--------------test용 버튼
                    FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();

                    String email = cur_user.getEmail();
                    String key = email.split("@")[0];   //key에 @는 저장이 안되므로 앞에 ID만 분리

                    DatabaseReference curRef = DB_Reference.parentRef.child(key);

                    BalloonStat newBalloon = new BalloonStat();
                    newBalloon.setInit();
                    
//                    curRef.child("child_list").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DataSnapshot> task) {
//                            ArrayList<String> child_list = new ArrayList<>();
//
//                            for (DataSnapshot child_key : task.getResult().getChildren()) {
//                                child_list.add(child_key.getValue(String.class));
//                            }
//                            if (child_list.size() == 1) {
//                                SetBalloon.setCurrentBalloon(child_list.get(0), newBalloon, false);
//                            } else if (child_list.size() == 0) {
//                                Log.e("doowon", "has no child! (can't make balloon)");
//                            } else {
//                                //                                for (String s : child_list) {} //다음값이 있는지 체크
//                                Log.e("doowon", "has 2 or more children\nchild_list" + child_list.toString());
//                            }
//                        }
//                    });

                    break;      //--------------------------
            }
        }
    };


    private void startMyActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startTimeCheck() {
        Intent serviceIntent = new Intent(this, ScreenService.class);
        serviceIntent.putExtra("state", "on");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent);
        else
            startService(serviceIntent);
        //finish();
    }

    private void stopTimeCheck() {
        int sec = 0;
        Intent serviceIntent = new Intent(this, ScreenService.class);
        getApplicationContext().stopService(serviceIntent); // stop service

    }

    private void isMatched() {                                                  // user가 매칭된 상태인지 파악
        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();
        String user_email = cur_user.getEmail();
        String user_key = user_email.split("@")[0];

        this.parentRef.child(user_key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("doowon", "Error getting data", task.getException());
                } else {
                    if (task.getResult().exists()) {
                        Log.d("DB", "User identity (부모)");
                        Parent parent = task.getResult().getValue(Parent.class);
                        if (parent.getChild_list() == null) {
                            Log.d("DB", "has no child");
                            // go to parent unmatched
                        } else {
                            Log.d("DB", "has child");
                            // go to parent matched
                        }
                    }
                }
            }
        });

        this.chidRef.child(user_key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("doowon", "Error getting data", task.getException());
                } else {
                    if (task.getResult().exists()) {
                        Log.d("DB", "User identity (자식)");
                        Child child = task.getResult().getValue(Child.class);
                        if(child.getCurrent_Balloon() == null) {
                            Log.d("DB", "has no parent");
                            // go to child unmatched
                        }
                        else {
                            Log.d("DB", "has parent");
                            // go to child matched
                        }
                    }
                }
            }
        });
    }

}