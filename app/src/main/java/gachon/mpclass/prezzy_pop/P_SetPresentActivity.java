package gachon.mpclass.prezzy_pop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

public class P_SetPresentActivity extends AppCompatActivity {

    ImageView imgView_present;
    Bitmap bitmap_present;
    Button btn_set_present; // 선물 정해지면 누르는 완료버튼
    EditText text_presentName;
    int REQUEST_IMAGE_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_set_present);

        text_presentName = findViewById(R.id.edit_presentName);
        imgView_present = findViewById(R.id.imgView_present);
        imgView_present.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_IMAGE_CODE); }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CODE)
        {
            // 사진 선택 했을때
            if(resultCode == RESULT_OK)
            {
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    bitmap_present = BitmapFactory.decodeStream(in);//비트맵으로 사진 받아오기
                    Bitmap img_resized = Bitmap.createScaledBitmap(bitmap_present, 700, 700, false);
                    in.close();

                    imgView_present.setImageBitmap(img_resized);//이미지뷰에 사진 보여줌
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
}