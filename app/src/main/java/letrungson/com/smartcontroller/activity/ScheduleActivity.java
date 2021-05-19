package letrungson.com.smartcontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    FloatingActionButton floating_action_btn;
    String roomId;
    ScheduleListView scheduleListView;
    private List<Schedule> lstSchedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        floating_action_btn = findViewById(R.id.floatingActionButton);
        ListView listView = findViewById(R.id.smart_schedule_listview);
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");

        //Setup Toolbar
        Toolbar toolbar = findViewById(R.id.schedule_toolbar);
        toolbar.setTitle("Smart Schedule");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getAllSchedule(roomId);
        scheduleListView = new ScheduleListView(getApplicationContext(), lstSchedule);
        listView.setAdapter(scheduleListView);

        floating_action_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleActivity.this, ScheduleSetActivity.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
                scheduleListView.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return false;
        }
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
