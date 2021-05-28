package gachon.mpclass.prezzy_pop.pushNoti;

import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class SendMessage {
    private static final String TAG = "Send Message";
    List<String> uidList = new ArrayList<>();
    List<String> tokenList = new ArrayList<>();
    FirebaseDatabase db;
    Handler handler= new Handler();
    String title;
    String message;

    public SendMessage(List uidList,String title,String message){
        this.uidList=uidList;
        this.title=title;
        this.message=message;
        store();// store token
    }

    private void store() {// extract token list to Users
        /*
        db=FirebaseDatabase.getInstance();

        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                UserToken userToken = new UserToken(
                                        doc.getString("uid"),
                                        doc.getString("token")
                                );
                                Log.d(TAG, doc.getId() + " => " + doc.getData());
                                if(uidList.contains(userToken.getUid())){
                                    addTokenList(userToken.getToken());
                                    Log.d(TAG, doc.getId() + " ===> " + userToken.getToken());
                                }
                            }
                            send(); // send to token user
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

         */
    }
    public void send(){
        Log.d(TAG, "Token List Size = "+tokenList.size());
        for(int i=0;i<tokenList.size();i++){
            SendNotification.sendNotification(tokenList.get(i), title, message);
            Log.d(TAG, "Send to "+tokenList.get(i));
        }
    }
    private void addTokenList(String newToken){
        tokenList.add(newToken);
    }
}
