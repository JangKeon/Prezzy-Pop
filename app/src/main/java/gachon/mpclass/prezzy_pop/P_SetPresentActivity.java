package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class P_SetPresentActivity extends AppCompatActivity {
    final String TAG = "P_SetPresentActivity";
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    ImageView imgView_present;
    Bitmap bitmap_present;
    Button btn_set_present; // 선물 정해지면 누르는 완료버튼
    EditText text_presentName;
    int REQUEST_IMAGE_CODE = 101;
    Animation cloudAmimation;
    ImageView cloud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_set_present);


        initPresentButton();


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

                if (id == R.id.history) {
                    Toast.makeText(context, "부모는 History를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    startToast("로그아웃 되었습니다");
                    startMyActivity(LoginActivity.class);
                }

                return true;
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Navigation Item selected
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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
//                        iv_review_image.setImageDrawable(reviewImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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