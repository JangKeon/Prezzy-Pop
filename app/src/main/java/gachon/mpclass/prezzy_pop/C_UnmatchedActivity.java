package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class C_UnmatchedActivity extends AppCompatActivity {
    DatabaseReference childRef = DB_Reference.childRef;
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_unmatched);
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
                    Toast.makeText(context, "아직은 History 를 열람할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }

                else if(id == R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    startToast("로그아웃 되었습니다");
                    startMyActivity(LoginActivity.class);
                }

                return true;
            }
        });
        findViewById(R.id.btn_logout).setOnClickListener(onClickListener);
        isMatched();
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
                        if(child.getCurrent_balloon_id() == null) {
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