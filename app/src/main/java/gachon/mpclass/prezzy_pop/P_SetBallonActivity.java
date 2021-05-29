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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_set_balloon);

        initToolbar();
        initBalloonButton();
        initPresentButton();

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

                    imgUpdateToDB(img_resized);

                    imgView_present.setImageBitmap(img_resized);//이미지뷰에 사진 보여줌
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
                    Toast.makeText(context, title + ": 로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
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

        Bitmap bitmap_balloon = BitmapFactory.decodeResource(getResources(),R.drawable.ballon);
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

    //DB에 저장하기---------------------------------------------
    public void imgUpdateToDB(Bitmap bitmap_img) {
        String string_img = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap_img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] reviewImage = stream.toByteArray();
        string_img = byteArrayToBinaryString(reviewImage);

        //DB에 추가하기

        DatabaseReference imgRef = DB_Reference.rootRef.child("imgTest");
        imgRef.setValue(string_img);

        imgRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                String img_btp = task.getResult().getValue(String.class);

                byte[] b = binaryStringToByteArray(img_btp);
                ByteArrayInputStream is = new ByteArrayInputStream(b);
                Drawable reviewImage = Drawable.createFromStream(is, "reviewImage");
                imgview_balloon.setImageDrawable(reviewImage);
            }
        });
    }

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
}