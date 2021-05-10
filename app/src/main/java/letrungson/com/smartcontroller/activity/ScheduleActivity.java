package letrungson.com.smartcontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Schedule;
import letrungson.com.smartcontroller.adapter.ScheduleListView;

public class ScheduleActivity extends AppCompatActivity {
    private final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    private List<Schedule> lstSchedule;
    ImageButton close_btn;

    ScheduleListView scheduleListView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setscheduled);
        close_btn = findViewById(R.id.imageButton);
        ListView listView = findViewById(R.id.smart_schedule_listview);
        getAllSchedule();
        scheduleListView = new ScheduleListView(getApplicationContext(), lstSchedule);
        listView.setAdapter(scheduleListView);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getAllSchedule(){
        lstSchedule = new ArrayList<>();
        Query allSchedule = firebaseDatabase.getReference("schedules");
        allSchedule.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstSchedule.clear();
                for(DataSnapshot data: snapshot.getChildren()){
                    Schedule schedule = data.getValue(Schedule.class);
                    String scheduleId = data.getKey();
                    schedule.setScheduleID(scheduleId);
                    lstSchedule.add(schedule);
                }
                scheduleListView.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
