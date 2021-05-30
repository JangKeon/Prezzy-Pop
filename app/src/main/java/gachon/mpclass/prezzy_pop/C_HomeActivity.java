package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;

import gachon.mpclass.prezzy_pop.service.ScreenService;

public class C_HomeActivity extends AppCompatActivity {
    private FirebaseUser cur_user;
    private String cur_email;
    private String cur_key;
    private String curBalloonID;

    private int cur_time;
    private int set_time;
    private DrawerLayout mDrawerLayout;
    private Context context = this;

    Animation cloud1_anim;
    Animation cloud2_anim;
    Animation cloud3_anim;
    Animation balloon_anim;
    ImageView cloud1_view;
    ImageView cloud2_view;
    ImageView cloud3_view;
    ImageView balloon_view;
    ImageView present_view;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_home);

        cur_user = FirebaseAuth.getInstance().getCurrentUser();
        cur_email = cur_user.getEmail();
        cur_key = cur_email.split("@")[0];

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.drawer_menu); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.history){
                    Toast.makeText(context, "History를 확인합니다.", Toast.LENGTH_SHORT).show();
                    startMyActivity(C_HistoryActivity.class);
                }

                else if(id == R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    startToast("로그아웃 되었습니다");
                    stopTimeCheck();
                    startMyActivity(LoginActivity.class);
                }

                return true;
            }
        });
        findViewById(R.id.img_balloon).setOnClickListener(onClickListener);
        findViewById(R.id.img_present).setOnClickListener(onClickListener);
        cloud1_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloudanim1);
        cloud1_view = findViewById(R.id.cloud1);
        cloud2_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloudanim2);
        cloud2_view = findViewById(R.id.cloud2);
        cloud3_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloudanim3);
        cloud3_view = findViewById(R.id.cloud3);
        balloon_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        balloon_view = findViewById(R.id.img_balloon);
        textView=findViewById(R.id.textView);
        present_view=findViewById(R.id.img_present);

        cloud1_view.startAnimation(cloud1_anim);
        cloud2_view.startAnimation(cloud2_anim);
        cloud3_view.startAnimation(cloud3_anim);
        balloon_view.startAnimation(balloon_anim);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();

        getCurBalloonID();
    }

    View.OnClickListener onClickListener = (v) -> {

        switch (v.getId()) {
            case R.id.img_balloon:
                Start_Stop();
                break;
            case R.id.img_present:
                openPresent();
                break;

        }
    };

    private void getCurBalloonID() {
        DatabaseReference childRef = DB_Reference.childRef.child(this.cur_key);
        DatabaseReference c_curBalloonRef = childRef.child("current_balloon_id");

        c_curBalloonRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                String curBalloonID = task.getResult().getValue(String.class);
                setCurBalloonID(curBalloonID);

                getSet_timeFromDB();
                initMissionList();
            }
        });
    }

    private void setCurBalloonID(String curBalloonID) {
        this.curBalloonID = curBalloonID;
    }

    private void initMissionList() {
        DatabaseReference missionRef = DB_Reference.missionRef.child(cur_key).child(curBalloonID);

        missionRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()) {
                    String missionTxt = snapshot.getValue(String.class);
//                    list_mission.add(missionTxt);
                }
            }
        });
    }

    private void getSet_timeFromDB() {
        DatabaseReference cur_balloonSet_timeRef = DB_Reference.balloonRef.child(cur_key).child(curBalloonID).child("set_time");

        cur_balloonSet_timeRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                int set_time = task.getResult().getValue(Integer.TYPE);
                setSet_time(set_time);

                setBalloonCur_timeChangeListener();
            }
        });
    }

    private void setSet_time(int set_time) {
        this.set_time = set_time;
    }

    private void setBalloonCur_timeChangeListener() {
        this.curBalloonID = curBalloonID;
        DatabaseReference cur_balloonCur_timeRef = DB_Reference.balloonRef.child(cur_key).child(curBalloonID).child("cur_time");
        DatabaseReference cur_balloonCur_stateRef = DB_Reference.balloonRef.child(cur_key).child(curBalloonID).child("state");

        cur_balloonCur_timeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int cur_time = snapshot.getValue(Integer.TYPE);
                setCur_time(cur_time);

                if(cur_time>=set_time){ // 목표 달성시
                    cur_time=set_time;
                    cur_balloonCur_stateRef.setValue("waiting"); // 상태변경
                    present_view.setVisibility(View.VISIBLE); // 선물 버튼 활성화
                    balloon_view.setVisibility(View.GONE); // 풍선 비활성화
                    textView.setText("선물이 도착했어요!\n선물을 클릭해 열어보세요");
                }

                Bitmap bitmap_balloon = BitmapFactory.decodeResource(getResources(),R.drawable.img_ballon);
                double resizewidth = 100 + (600 * ((double)cur_time/set_time));// 풍선 최소 크기 100, 최대300
                balloon_view.setImageBitmap(bitmap_resize(bitmap_balloon,resizewidth));
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setCur_time(int cur_time) {
        this.cur_time = cur_time;
    }

    private void startTimeCheck() {
        Intent serviceIntent = new Intent(this, ScreenService.class);
        serviceIntent.putExtra("state", "on");

        SharedPreferences sharedPreferences=getSharedPreferences("Child",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isStarted",true);
        editor.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent);
        else
            startService(serviceIntent);
        textView.setText("풍선을 다시 누르면 멈출 수 있어요");

    }
    private void stopTimeCheck() {
        Intent serviceIntent = new Intent(this, ScreenService.class);

        SharedPreferences sharedPreferences=getSharedPreferences("Child",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isStarted",false);
        editor.commit();
        getApplicationContext().stopService(serviceIntent); // stop service
        textView.setText("풍선을 눌러 풍선을 키워보세요!");
    }

    private void Start_Stop(){
        SharedPreferences sharedPreferences=getSharedPreferences("Child",MODE_PRIVATE);
        boolean isStart = sharedPreferences.getBoolean("isStarted",false);
        if(isStart){
            stopTimeCheck();
        }else{
            startTimeCheck();
        }

    }
    private void openPresent(){

    }
    // CurTime에 따라 비율유지하면서 풍선 크기 변경
    private Bitmap bitmap_resize(Bitmap bitmap, double resizeWidth){
        double aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(bitmap, (int)resizeWidth, targetHeight, false);
        return result;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    //DB에서 가져오기------------------------------------------------
    // 스트링을 바이너리 바이트 배열로
    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    // 스트링을 바이너리 바이트로
    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }

    public void selectFirebase(int index) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("reviews/" + index).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals("image")) {
                        String image = dataSnapshot.getValue().toString();
                        byte[] b = binaryStringToByteArray(image);
                        ByteArrayInputStream is = new ByteArrayInputStream(b);
                        Drawable reviewImage = Drawable.createFromStream(is, "reviewImage");
//                        imgview_balloon.setImageDrawable(reviewImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void startMyActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}