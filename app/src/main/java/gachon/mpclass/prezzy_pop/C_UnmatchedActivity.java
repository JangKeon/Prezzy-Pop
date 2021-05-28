package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class C_UnmatchedActivity extends AppCompatActivity {
    DatabaseReference childRef = DB_Reference.childRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_unmatched);
        findViewById(R.id.btn_logout).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener= (v)->{

        switch(v.getId()){
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startToast("로그아웃 되었습니다");
                startMyActivity(LoginActivity.class);
                break;
        }
    };

    private void isMatched() {                                                  // user가 매칭된 상태인지 파악
        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();
        String user_email = cur_user.getEmail();
        String user_key = user_email.split("@")[0];

        this.childRef.child(user_key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("doowon", "Error getting data", task.getException());
                } else {
                    if (task.getResult().exists()) {
                        Log.d("DB", "User identity (자식)");
                        Child child = task.getResult().getValue(Child.class);
                        if(child.getCurrent_Balloon() == null) {
                            // do nothing
                        }
                        else {
                            Log.d("DB", "has parent");
                            startMyActivity(C_HomeActivity.class);
                        }
                    }
                }
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