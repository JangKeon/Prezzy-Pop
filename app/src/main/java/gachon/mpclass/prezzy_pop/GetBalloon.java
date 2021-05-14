package gachon.mpclass.prezzy_pop;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class GetBalloon {
    public static void getHistoryAddress(DatabaseReference historyRef) {
        ArrayList<String> history_adr_list = new ArrayList<>();

        historyRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot adr : task.getResult().getChildren()) {
                    history_adr_list.add(adr.getValue(String.class));
                }
            }
        });
    }
}
