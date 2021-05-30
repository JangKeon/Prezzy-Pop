package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class P_HomeActivity extends AppCompatActivity {
    static final String TAG = "P_HomeActivity";
    private FirebaseUser cur_user;
    private String parent_email;
    private String parent_key;
    private String curBalloonID;
    private String child_key;

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    private int cur_time;
    private int set_time;

    ListView listView_mission;
    ArrayAdapter<String> adapter;
    ArrayList<String> list_mission;
    EditText edit_mission;
    Button btn_addMission;
    Button btn_logout;
    Button btn_setBalloon;
    ImageView imgView_balloon;
    TextView text_rate;

    Animation cloud1_anim;
    Animation cloud2_anim;
    Animation cloud3_anim;
    Animation balloon_anim;
    ImageView cloud1_view;
    ImageView cloud2_view;
    ImageView cloud3_view;
    ImageView balloon_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_home);
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
                    Toast.makeText(context,"부모는 History를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }

                else if(id == R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    startToast("로그아웃 되었습니다");
                    startMyActivity(LoginActivity.class);
                }
                return true;
            }
        });
        cur_user = FirebaseAuth.getInstance().getCurrentUser();
        parent_email = cur_user.getEmail();
        parent_key = parent_email.split("@")[0];

        cloud1_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloudanim1);
        cloud1_view = findViewById(R.id.cloud1);
        cloud2_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloudanim2);
        cloud2_view = findViewById(R.id.cloud2);
        cloud3_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloudanim3);
        cloud3_view = findViewById(R.id.cloud3);
        balloon_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        balloon_view = findViewById(R.id.img_balloon);


        cloud1_view.startAnimation(cloud1_anim);
        cloud2_view.startAnimation(cloud2_anim);
        cloud3_view.startAnimation(cloud3_anim);
        balloon_view.startAnimation(balloon_anim);
        imgView_balloon = findViewById(R.id.img_balloon);

        edit_mission = findViewById(R.id.edit_mission);
        btn_addMission = findViewById(R.id.btn_addMission);
        btn_logout = findViewById(R.id.btn_logout);
        btn_setBalloon = findViewById(R.id.btn_setballoon);

        list_mission = new ArrayList<String>();
        list_mission.add("설거지 도와드리기");


        btn_setBalloon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 풍선 설정
                startMyActivity(P_SetBallonActivity.class);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startToast("로그아웃 되었습니다");
                startMyActivity(LoginActivity.class);
            }
        });
        btn_addMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String missionTxt = edit_mission.getText().toString();
                list_mission.add(missionTxt);
                adapter.notifyDataSetChanged();
                edit_mission.setText("");

                setMissionToDB(missionTxt);
            }
        });

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list_mission);
        listView_mission = findViewById(R.id.listView_mission);
        listView_mission.setAdapter(adapter);
        listView_mission.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // 자녀가 임무 성공시 아이템 클릭하면 팝업창으로 자녀가 임무를 수행했나요? 예/아니요
                // -> 자녀에게 풍선커짐 알림, 풍선스탯 키우고, 자녀 화면에서 V 표시(성공표시)

                deleteMissionToDB(list_mission.get(0));
                list_mission.remove(i);
                adapter.notifyDataSetChanged();
            }
        });
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

        getChild_list();
    }

    // SetTime에 따라 비율유지하면서 풍선 크기 변경
    private Bitmap bitmap_resize(Bitmap bitmap, double resizeWidth){
        double aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(bitmap, (int)resizeWidth, targetHeight, false);
        return result;
    }

    private void getChild_list() {
        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();
        String parent_email = cur_user.getEmail();
        String parent_key = parent_email.split("@")[0];

        DatabaseReference parentRef = DB_Reference.parentRef.child(parent_key);
        DatabaseReference p_child_listRef = parentRef.child("child_list");

        p_child_listRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                ArrayList<String> child_list = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String childID = snapshot.getValue(String.class);
                    child_list.add(childID);
                }
                setChild_key(child_list.get(0));
                getCurBalloonID();
            }
        });
    }

    private void setChild_key(String child_key) {
        this.child_key = child_key;
    }

    private void getCurBalloonID() {
        DatabaseReference childRef = DB_Reference.childRef.child(this.child_key);
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

    private void setMissionToDB(String missionTxt) {
        DatabaseReference missionRef = DB_Reference.missionRef.child(child_key);
        DatabaseReference curBalloonMissionRef = missionRef.child(curBalloonID);

        curBalloonMissionRef.push().setValue(missionTxt);
    }

    private void deleteMissionToDB(String missionTxt) {
        DatabaseReference missionRef = DB_Reference.missionRef.child(child_key);
        DatabaseReference curBalloonMissionRef = missionRef.child(curBalloonID);

        curBalloonMissionRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()) {
                    if(snapshot.getValue(String.class).equals(missionTxt)) {
                        snapshot.getRef().setValue(null);
                    }
                }
            }
        });
    }


        @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void initMissionList() {
        DatabaseReference missionRef = DB_Reference.missionRef.child(child_key).child(curBalloonID);

        missionRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()) {
                    String missionTxt = snapshot.getValue(String.class);
                    list_mission.add(missionTxt);
                }
            }
        });
    }

    private void getSet_timeFromDB() {
        DatabaseReference cur_balloonSet_timeRef = DB_Reference.balloonRef.child(child_key).child(curBalloonID).child("set_time");

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

    private void setBalloonCur_timeChangeListener() {      //cur time listener
        DatabaseReference cur_balloonCur_timeRef = DB_Reference.balloonRef.child(child_key).child(curBalloonID).child("cur_time");
        text_rate = findViewById(R.id.text_rate);

        cur_balloonCur_timeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    int cur_time = snapshot.getValue(Integer.TYPE);
                    setCur_time(cur_time);

                    // 데이터 변화 있을 때마다 풍선크기 resize
                    Bitmap bitmap_balloon = BitmapFactory.decodeResource(getResources(),R.drawable.img_ballon);
                    double rate=(double)cur_time/set_time;
                    double resizewidth = 100 + (600 * rate);// 풍선 최소 크기 100, 최대500
                    imgView_balloon.setImageBitmap(bitmap_resize(bitmap_balloon,resizewidth));
                    String rateText=Integer.toString((int)(rate*100))+"%";
                    text_rate.setText(rateText);

                    Log.e("doowon", "onChildChange : " + snapshot.getValue().toString());

                    if(cur_time>=set_time){ // 목표 달성시
                        text_rate.setText("100%");
                        btn_setBalloon.setVisibility(View.VISIBLE); // 버튼 활성화
                    }

                }
                else {
                    Log.e(TAG, "Data change listener get null");
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setCur_time(int cur_time) {
        this.cur_time = cur_time;
    }

    private void startMyActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}