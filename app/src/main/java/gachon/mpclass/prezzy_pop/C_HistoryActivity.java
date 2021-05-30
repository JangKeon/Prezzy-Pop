package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class C_HistoryActivity extends AppCompatActivity {
    static final int IMG_VIEW_NUM = 8;
    static final String TAG = "C_HistoryActivity";

    private FirebaseUser cur_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_history);
        cur_user = FirebaseAuth.getInstance().getCurrentUser();

        getHistory();
    }

    private void getHistory() {
        String child_email = cur_user.getEmail();
        String child_key = child_email.split("@")[0];

        ArrayList<BalloonStat> historyList = new ArrayList<>();
        DatabaseReference balloonRef = DB_Reference.balloonRef.child(child_key);

        balloonRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()) {
                    BalloonStat history = snapshot.getValue(BalloonStat.class);
                    historyList.add(history);
                }
                setHistory(historyList);
            }
        });
    }

    private void setHistory(ArrayList<BalloonStat> historyList) {
        ArrayList<Integer> initIndex = new ArrayList<>();

        for(int i = 0; i < historyList.size(); ++i) {
            if(historyList.get(i).getState().equals("init")) {
                initIndex.add(i);
            }
        }

        for(int i = 0; i < initIndex.size(); ++i) {
            historyList.remove(initIndex.get(i));
        }

        for(int i = 1; i <= historyList.size(); ++i) {
            int imgViewID = getResources().getIdentifier("imgView_history" + i, "id", getPackageName());
            int txtViewID = getResources().getIdentifier("txtView_history" + i, "id", getPackageName());

            ImageView imgView = findViewById(imgViewID);
            TextView txtView = findViewById(txtViewID);

            setImgView(imgView, historyList.get(i-1).getImage());
            setTxtView(txtView, historyList.get(i-1).getAchievement() + "(" + historyList.get(i-1).getSet_time() + ")");
        }
    }

    private void setImgView(ImageView imgView ,String image) {
        byte[] b = binaryStringToByteArray(image);
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        Drawable reviewImage = Drawable.createFromStream(is, "reviewImage");
        imgView.setImageDrawable(reviewImage);
    }

    private void setTxtView(TextView txtView, String text) {
        txtView.setText(text);
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
}