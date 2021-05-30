package gachon.mpclass.prezzy_pop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OpenPresentActivity extends AppCompatActivity {
    Button btn_openPresent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_present);
        btn_openPresent = findViewById(R.id.btn_confirmPresent);

        btn_openPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startToast("상품이 수령되었습니다");
                startMyActivity(MainActivity.class);
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