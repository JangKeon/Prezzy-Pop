package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.annotations.NotNull;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class P_SetBallonActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    ImageView imgview_balloon;
    SeekBar seekBar;// 풍선크기 정하는 seekBar
    TextView seekBarValue;// 풍선크기 텍스트로 보여주기 위함
    int totalRate;// 총 풍선크기

    ImageView imgView_present;
    Bitmap bitmap_present;
    Button btn_set_present; // 선물 정해지면 누르는 완료버튼
    EditText text_presentName;
    int REQUEST_IMAGE_CODE = 101;

    ScrollView scrollView;

    private FirebaseUser cur_user;

    private String string_img;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_set_balloon);
        cur_user = FirebaseAuth.getInstance().getCurrentUser();

        initToolbar();
        initBalloonButton();
        initPresentButton();

        btn_set_present = findViewById(R.id.btn_setBalloon);

        btn_set_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cur_email = cur_user.getEmail();
                String cur_key = cur_email.split("@")[0];

                DatabaseReference userRef = DB_Reference.parentRef.child(cur_key);
                DatabaseReference child_listRef = userRef.child("child_list");

                child_listRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        HashMap<String, String> child_listMap =  task.getResult().getValue(new GenericTypeIndicator<HashMap<String, String>>() {});
                        ArrayList<String> child_list = new ArrayList<>();

                        for(String child_key_iter : child_listMap.values()) {
                            child_list.add(child_key_iter);
                        }

                        String child_key = child_list.get(0);      //첫번째 child key 가져오기

                        makeBalloon(cur_key, child_key);
                        startToast("아이에게 풍선이 전달되었습니다");
                        startMyActivity(MainActivity.class);
                    }
                });
            }
        });
        ImageView pointDown = findViewById(R.id.img_pointdown);

        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(500);        // 에니메이션 동작 주기
        anim.setRepeatCount(-1);    // 에니메이션 반복 회수
        anim.setRepeatMode(Animation.REVERSE);// 반복하는 방법
        pointDown.startAnimation(anim);


        TextView text = findViewById(R.id.textView8);
        scrollView = findViewById(R.id.scrollView_setBalloon);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("scroll",String.valueOf(scrollY));
                Log.d("height",String.valueOf(v.getMeasuredHeight()));
                Log.d("min",String.valueOf(v.getMinimumHeight()));

                float bottomvalue = ((float)scrollY/v.getMeasuredHeight());
                float topvalue = (1 - (float)scrollY/v.getMeasuredHeight());
                pointDown.setAlpha(topvalue);
                seekBarValue.setAlpha(topvalue);
                text.setAlpha(bottomvalue);
            }
        });

    }

    @Override       //네비게이션바 선택시
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 이미지 비율 유지하면서 풍선 size 변경
    private Bitmap bitmap_resize(Bitmap bitmap, int resizeWidth){
        double aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(bitmap, resizeWidth, targetHeight, false);
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CODE) {
            // 사진 선택 했을때
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    bitmap_present = BitmapFactory.decodeStream(in);//비트맵으로 사진 받아오기
                    Bitmap img_resized = Bitmap.createScaledBitmap(bitmap_present, 700, 700, false);
                    in.close();

                    imgView_present.setImageBitmap(img_resized);//이미지뷰에 사진 보여줌

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    img_resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] reviewImage = stream.toByteArray();
                    string_img = byteArrayToBinaryString(reviewImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initToolbar() {
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

                if(id == R.id.account){
                    Toast.makeText(context, title + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.setting){
                    Toast.makeText(context, title + ": 설정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    startToast("로그아웃 되었습니다");
                    startMyActivity(LoginActivity.class);
                }

                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initBalloonButton() {
        imgview_balloon = findViewById(R.id.img_balloon);
        seekBar = findViewById(R.id.bar_setBalloon);
        seekBarValue = findViewById(R.id.text_totalRate);

        Bitmap bitmap_balloon = BitmapFactory.decodeResource(getResources(),R.drawable.img_ballon);
        imgview_balloon.setImageBitmap(bitmap_resize(bitmap_balloon,300));

        seekBarValue.setText("100");
        seekBar.setMax(1000);
        seekBar.setMin(100);
        totalRate = 100;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // onProgressChange - Seekbar 값 변경될때마다 호출
                seekBarValue.setText(String.valueOf(seekBar.getProgress()));
                int resizeWidth = 300 + (seekBar.getProgress() / 10);
                bitmap_resize(bitmap_balloon,resizeWidth);
                imgview_balloon.setImageBitmap(bitmap_resize(bitmap_balloon,resizeWidth));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // onStartTeackingTouch - SeekBar 값 변경위해 첫 눌림에 호출
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // onStopTrackingTouch - SeekBar 값 변경 끝나고 드래그 떼면 호출
                seekBarValue.setText(String.valueOf(seekBar.getProgress()));
                totalRate = seekBar.getProgress();
                //Log.v("rate",String.valueOf(totalRate));
            }
        });
    }

    private void initPresentButton() {
        text_presentName = findViewById(R.id.edit_presentName);
        imgView_present = findViewById(R.id.imgView_present);

        imgView_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_IMAGE_CODE);
            }
        });
    }

    private void makeBalloon(String parent_key, String child_key) {
        Date now = new Date();
        String strNow = DateString.DateToString(now);

        String achievement = text_presentName.getText().toString();
        int cur_time = 0;
        String date = strNow;
        int set_time = totalRate;
        String state = "default";
        String image = string_img;

        BalloonStat newBalloon = new BalloonStat(child_key, set_time, achievement, date, parent_key, cur_time, state, image);

        SetBalloon.setCurrentBalloon(child_key, newBalloon);
    }

    //DB에 저장하기---------------------------------------------

    // 바이너리 바이트 배열을 스트링으로
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    // 바이너리 바이트를 스트링으로
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    private void startMyActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}