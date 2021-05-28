package gachon.mpclass.prezzy_pop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import gachon.mpclass.prezzy_pop.service.ScreenService;

public class C_HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_home);

        findViewById(R.id.btn_logout).setOnClickListener(onClickListener);
        findViewById(R.id.btn_start).setOnClickListener(onClickListener);
        findViewById(R.id.btn_stop).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener= (v)->{

        switch(v.getId()){
            case R.id.btn_logout:
                stopTimeCheck();
                FirebaseAuth.getInstance().signOut();
                startToast("로그아웃 되었습니다");
                startMyActivity(LoginActivity.class);
                break;
            case R.id.btn_start:
                startTimeCheck();
                break;
            case R.id.btn_stop:
                stopTimeCheck();
                break;
        }

    };

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
        Intent serviceIntent = new Intent(this, ScreenService.class);
        getApplicationContext().stopService(serviceIntent); // stop service
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void startMyActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}