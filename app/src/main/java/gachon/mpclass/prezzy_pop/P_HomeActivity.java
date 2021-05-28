package gachon.mpclass.prezzy_pop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class P_HomeActivity extends AppCompatActivity {
        ListView listView_mission;
        ArrayAdapter<String> adapter;
        ArrayList<String> list_mission;

        EditText edit_mission;
        Button btn_addMission;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            edit_mission = findViewById(R.id.edit_mission);
            btn_addMission = findViewById(R.id.btn_addMission);

            list_mission = new ArrayList<String>();
            list_mission.add("설거지 도와드리기");


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
}