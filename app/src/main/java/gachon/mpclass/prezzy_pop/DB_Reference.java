package gachon.mpclass.prezzy_pop;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DB_Reference {
    public static DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference childRef = rootRef.child("Users").child("Children");
    public static DatabaseReference parentRef = rootRef.child("Users").child("Parent");
    public static DatabaseReference balloonRef = rootRef.child("Balloons");
}