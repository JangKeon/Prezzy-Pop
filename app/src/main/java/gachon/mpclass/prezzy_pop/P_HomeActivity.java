package gachon.mpclass.prezzy_pop;

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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class P_HomeActivity extends AppCompatActivity {
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
        ImageView cloud1_view;
        ImageView cloud2_view;
        ImageView cloud3_view;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_p_home);

            cloud1_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.cloudanim1);
            cloud1_view = findViewById(R.id.cloud1);
            cloud2_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.cloudanim2);
            cloud2_view = findViewById(R.id.cloud2);
            cloud3_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.cloudanim3);
            cloud3_view = findViewById(R.id.cloud3);

            cloud1_view.startAnimation(cloud1_anim);
            cloud2_view.startAnimation(cloud2_anim);
            cloud3_view.startAnimation(cloud3_anim);

            edit_mission = findViewById(R.id.edit_mission);
            btn_addMission = findViewById(R.id.btn_addMission);
            btn_logout = findViewById(R.id.btn_logout);
            btn_setBalloon=findViewById(R.id.btn_setballoon);

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