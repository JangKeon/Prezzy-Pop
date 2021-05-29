package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class P_HomeActivity extends AppCompatActivity {
    private FirebaseUser cur_user;
    private String cur_email;
    private String cur_key;
    private String curBalloonID;

    private int cur_time;
    private int set_time;

    ListView listView_mission;
    ArrayAdapter<String> adapter;
    ArrayList<String> list_mission;
    EditText edit_mission;
    Button btn_addMission;
    Button btn_logout;
    Button btn_setBalloon;

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

        cur_user = FirebaseAuth.getInstance().getCurrentUser();
        cur_email = cur_user.getEmail();
        cur_key = cur_email.split("@")[0];

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

                list_mission.add(edit_mission.getText().toString());
                adapter.notifyDataSetChanged();
                edit_mission.setText("");
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

                list_mission.remove(i);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference parentRef = DB_Reference.parentRef.child(cur_key);

        DatabaseReference child_listRef = parentRef.child("current_balloon_id");

        child_listRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                HashMap<String, String> child_listMap =  task.getResult().getValue(new GenericTypeIndicator<HashMap<String, String>>() {});
                ArrayList<String> child_list = new ArrayList<>();

                for(String child_key_iter : child_listMap.values()) {
                    child_list.add(child_key_iter);
                }

                String child_key = child_list.get(0);

                DatabaseReference childBalloonIdRef = DB_Reference.childRef.child(child_key).child("current_balloon_id");

                childBalloonIdRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        String curBalloonID = task.getResult().getValue(String.class);
                        setBalloonCur_timeChangeListener(curBalloonID);
                        getSet_timeFromDB(curBalloonID);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void getSet_timeFromDB(String curBalloonID) {
        DatabaseReference cur_balloonSet_timeRef = DB_Reference.balloonRef.child(cur_key).child(curBalloonID).child("set_time");

        cur_balloonSet_timeRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                int set_time = task.getResult().getValue(Integer.TYPE);
                setSet_time(set_time);
            }
        });
    }

    private void setSet_time(int set_time) {
        this.set_time = set_time;
    }

    private void setBalloonCur_timeChangeListener(String curBalloonID) {
        this.curBalloonID = curBalloonID;
        DatabaseReference cur_balloonCur_timeRef = DB_Reference.balloonRef.child(cur_key).child(curBalloonID).child("cur_time");

        cur_balloonCur_timeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {}

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                int cur_time = snapshot.getValue(Integer.TYPE);
                setCur_time(cur_time);
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
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