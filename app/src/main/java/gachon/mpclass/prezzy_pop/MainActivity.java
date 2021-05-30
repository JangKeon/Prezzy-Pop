package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    DatabaseReference parentRef = DB_Reference.parentRef;
    DatabaseReference childRef = DB_Reference.childRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ImageView loading = findViewById(R.id.img_loading);
        ImageView balloons = findViewById(R.id.img_balloons);
        Glide.with(this).load(R.raw.loading).into(loading);
        Glide.with(this).load(R.raw.balloons).into(balloons);
        // 로그인 된 상태가 아니라면
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startMyActivity(LoginActivity.class);
        } else {
            refreshToken();
            isMatched();
        }


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
                            startMyActivity(P_UnmatchedActivity.class);
                        } else {
                            Log.d("DB", "has child");
                            startMyActivity(P_HomeActivity.class);
                        }
                    }
                }
            }
        });

        this.childRef.child(user_key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("doowon", "Error getting data", task.getException());
                } else {
                    if (task.getResult().exists()) {
                        Log.d("DB", "User identity (자식)");
                        Child child = task.getResult().getValue(Child.class);
                        if(child.getCurrent_balloon_id() == null) {
                            Log.d("DB", "has no parent");
                            startMyActivity(C_UnmatchedActivity.class);
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
    private void refreshToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();

                        // store to db
                        FirebaseAuth mAuth=FirebaseAuth.getInstance();
                        String email=mAuth.getCurrentUser().getEmail();
                        String key = email.split("@")[0];

                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference tokenRef=rootRef.child("Tokens").child(key);
                        tokenRef.child("token").setValue(token);
                        tokenRef.child("key").setValue(key);
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