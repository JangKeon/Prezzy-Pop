package gachon.mpclass.prezzy_pop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class P_SetBallonActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    ImageView imgview_balloon;
    SeekBar seekBar;// 풍선크기 정하는 seekBar
    TextView seekBarValue;// 풍선크기 텍스트로 보여주기 위함
    int totalRate;// 총 풍선크기

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_set_balloon);

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
                    Toast.makeText(context, title + ": 로그아웃 시도중", Toast.LENGTH_SHORT).show();
                }

                return true;
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

    // 이미지 비율 유지하면서 size 변경
    private Bitmap bitmap_resize(Bitmap bitmap, int resizeWidth){
        double aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(bitmap, resizeWidth, targetHeight, false);
        return result;
    }
}