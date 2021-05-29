package gachon.mpclass.prezzy_pop.pushNoti;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import gachon.mpclass.prezzy_pop.DB_Reference;
import gachon.mpclass.prezzy_pop.MainActivity;

public class SendMessage {
    private static final String TAG = "Send Message";
    List<String> userList = new ArrayList<>();
    FirebaseAuth auth;
    String title;
    String message;

    public SendMessage(String title,String message){
        this.title=title;
        this.message=message;
        findChild();// store token
    }

    private void findChild() {// extract token list to Users
        String email=auth.getCurrentUser().getEmail();
        String parentKey = email.split("@")[0];

        DatabaseReference userRef = DB_Reference.parentRef.child(parentKey);
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
                findToken(child_key);
            }
        });


    }
    public void findToken(String childKey){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tokenRef=rootRef.child("Tokens").child(childKey).child("token");
        tokenRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                String childToken=task.getResult().getValue().toString();
                send(childToken);
            }
        });

    }
    public void send(String token){
        SendNotification.sendNotification(token, title, message);

    }

    private void addTokenList(String newToken){
        userList.add(newToken);
    }
}
