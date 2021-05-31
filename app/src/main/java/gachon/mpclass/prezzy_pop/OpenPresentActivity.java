package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class OpenPresentActivity extends AppCompatActivity {
    Button btn_openPresent;
    ImageView imgViewPresent;
    TextView txtViewPresent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_present);
        btn_openPresent = findViewById(R.id.btn_confirmPresent);
        imgViewPresent = findViewById(R.id.imageViewPresent);
        txtViewPresent = findViewById(R.id.txtViewPresent);

        btn_openPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startToast("상품이 수령되었습니다");
                startMyActivity(MainActivity.class);
            }
        });

        final KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
        konfettiView.build()
                .addColors( Color.argb(1,41,205,255),
                        Color.argb(1,120,255,68),
                        Color.argb(1,168,100,253) ,
                        Color.argb(1,255,113,141),
                        Color.argb(1,253,255,106))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 3000L);
    }

    @Override
    protected void onStart() {
        super.onStart();
        get_setPresentImageTxt();
    }

    private void get_setPresentImageTxt() {
        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();
        String email = cur_user.getEmail();
        String key = email.split("@")[0];

        DatabaseReference childRef = DB_Reference.childRef.child(key).child("current_balloon_id");

        childRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                String curBalloonID = task.getResult().getValue(String.class);

                DatabaseReference balloonRef = DB_Reference.balloonRef.child(key).child(curBalloonID);

                balloonRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        BalloonStat curBalloon = task.getResult().getValue(BalloonStat.class);

                        setPresentImageTxt(curBalloon);
                    }
                });
            }
        });
    }

    private void setPresentImageTxt(BalloonStat curBalloon) {
        String image = curBalloon.getImage();
        String achievement = curBalloon.getAchievement();

        byte[] b = binaryStringToByteArray(image);
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        Drawable reviewImage = Drawable.createFromStream(is, "reviewImage");
        imgViewPresent.setImageDrawable(reviewImage);

        txtViewPresent.setText(achievement);
    }

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

    private void startMyActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}