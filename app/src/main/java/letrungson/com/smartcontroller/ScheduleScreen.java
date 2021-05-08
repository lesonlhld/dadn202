package letrungson.com.smartcontroller;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ScheduleScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setscheduled);
        ListView listView = findViewById(R.id.smart_schedule_listview);
        List<Schedule> lst = getListSchedule();
        ScheduleListView scheduleListView = new ScheduleListView(getApplicationContext(),lst);
        listView.setAdapter(scheduleListView);
    }


    private List<Schedule> getListSchedule(){
        List<Schedule> lst = new ArrayList<Schedule>();
        Schedule schedule = new Schedule("Fri","Sat",25,70,"7:00","12:00");
        Schedule schedule1 = new Schedule("Mon","Sat",15,50,"13:00","12:00");
        Schedule schedule2 = new Schedule("Tue","Sat",22,90,"10:00","12:00");
        lst.add(schedule);
        lst.add(schedule1);
        lst.add(schedule2);
        return lst;
    }
}
