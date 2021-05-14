package letrungson.com.smartcontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.adapter.ScheduleListView;
import letrungson.com.smartcontroller.model.Schedule;

public class ScheduleActivity extends AppCompatActivity {
    private final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    ImageButton close_btn;
    TextView room;
    ScheduleListView scheduleListView;
    private List<Schedule> lstSchedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        close_btn = findViewById(R.id.imageButton);
        ListView listView = findViewById(R.id.smart_schedule_listview);
        Intent intent = getIntent();
        String roomId = intent.getStringExtra("roomId");
        String roomName = intent.getStringExtra("roomName");
        room = findViewById(R.id.textView4);
        room.setText(roomName);

        getAllSchedule(roomId);
        scheduleListView = new ScheduleListView(getApplicationContext(), lstSchedule);
        listView.setAdapter(scheduleListView);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getAllSchedule(String roomId) {
        lstSchedule = new ArrayList<>();
        Query allSchedule = firebaseDatabase.getReference("schedules").orderByChild("roomId").equalTo(roomId);
        allSchedule.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lstSchedule.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Schedule schedule = data.getValue(Schedule.class);
                    String scheduleId = data.getKey();
                    schedule.setScheduleId(scheduleId);
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
