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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import gachon.mpclass.prezzy_pop.service.ScreenService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unmatched);

        // 로그인 된 상태가 아니라면
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
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


    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            switch (v.getId()){
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
                case R.id.button:   //--------------test용
                    DatabaseReference childRef = DB_Reference.childRef;
                    childRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("", "Error getting data", task.getException());
                            } else {


//                                Cur_balloon cur = group_list.get("111111");
                            }
                        }
                    });

                    break;      //--------------------------
            }
        }
    };


    private void startMyActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }
    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void startTimeCheck(){
        Intent serviceIntent=new Intent(this, ScreenService.class);
        serviceIntent.putExtra("state","on");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent);
        else
            startService(serviceIntent);
        //finish();
    }
    private void stopTimeCheck(){
        int sec=0;
        Intent serviceIntent=new Intent(this, ScreenService.class);
        getApplicationContext().stopService(serviceIntent); // stop service

    }

}