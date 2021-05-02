package gachon.mpclass.prezzy_pop;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DB_Reference {
    public static DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Children");
    public static DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parent");
}