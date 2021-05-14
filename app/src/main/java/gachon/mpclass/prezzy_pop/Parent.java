package gachon.mpclass.prezzy_pop;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class Parent extends User{
    private HashMap<Integer, String> child_list;

    Parent() {}

    Parent(String key, String nick, String domain) {
        super(key, nick, domain);
    }

    public HashMap<Integer, String> getChild_list() {
        return child_list;
    }

    public void setChild_list(HashMap<Integer, String> child_list) {
        this.child_list = child_list;
    }

    public void addChild_list(String child_email_key) {
        this.child_list.put(this.child_list.size()+1, child_email_key);
    }

    public void getChild_list_fromDB() {
        DatabaseReference parentRef = DB_Reference.parentRef;
        Parent parent = this;
        DatabaseReference userRef = parentRef.child(super.getKey());

        userRef.child("child_list").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {       //child data 읽어오기
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("doowon", "Error getting data", task.getException());
                } else {
                    HashMap child_list =  task.getResult().getValue(HashMap.class);
                    parent.setChild_list(child_list);
                }
            }
        });
    }

    public void setChild_list_fromDB(HashMap child_list) {
        DatabaseReference parentRef = DB_Reference.parentRef;
        Parent parent = this;
        parentRef.setValue(child_list);
    }

}
