package letrungson.com.smartcontroller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Schedule;
import letrungson.com.smartcontroller.model.Room;

import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleEditActivity extends AppCompatActivity {
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    Schedule thisSchedule;
    String scheduleId;
    TextView start_time, end_time, temp_data, humi_data;
    ImageButton up_temp_btn, down_temp_btn, up_humi_btn, down_humi_btn, close_btn, tick_btn;
    Button set_repeatTime_btn, delete_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        scheduleId = intent.getStringExtra("scheduleId");
        getSchedule(scheduleId);

        setContentView(R.layout.activity_editschedule);

        start_time = (TextView) findViewById(R.id.start_time_text);
        end_time = (TextView) findViewById(R.id.end_time_text);
        up_temp_btn = (ImageButton) findViewById(R.id.up_temp_btn);
        down_temp_btn = (ImageButton) findViewById(R.id.down_temp_btn);
        up_humi_btn = (ImageButton) findViewById(R.id.up_humi_btn);
        down_humi_btn = (ImageButton) findViewById(R.id.down_humi_btn);
        temp_data = (TextView) findViewById(R.id.temp_data_view);
        humi_data = (TextView) findViewById(R.id.humi_data_view);
        close_btn = (ImageButton) findViewById(R.id.close_btn);
        tick_btn = (ImageButton) findViewById(R.id.tick_btn);
        set_repeatTime_btn = (Button) findViewById(R.id.set_repeatTime_btn);


        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;
                setTime(flag);
            }
        });

        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 1;
                setTime(flag);
            }
        });

        up_temp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 1;
                changeNum(v, flag);
            }
        });

        down_temp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 2;
                changeNum(v, flag);
            }
        });

        up_humi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 3;
                changeNum(v, flag);
            }
        });

        down_humi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 4;
                changeNum(v, flag);
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tick_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSchedule();
                finish();
            }
        });
        // lines below is prepare to set repeat day
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        set_repeatTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                final ArrayList itemsSelected = new ArrayList();
                boolean yetChecked[] = new boolean[7];
                for(int i = 0; i < 7; i++){
                    if (thisSchedule.getRepeatDay().toCharArray()[i] == '1'){
                        yetChecked[i] = true;
                        itemsSelected.add(i);
                    }
                    else {
                        yetChecked[i] = false;
                    }
                }
                builder.setTitle("Choose day:");
                builder.setMultiChoiceItems(items, yetChecked,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedItemId,
                                                boolean isSelected) {
                                if (isSelected) {
                                    itemsSelected.add(selectedItemId);
                                } else if (itemsSelected.contains(selectedItemId)) {
                                    itemsSelected.remove(Integer.valueOf(selectedItemId));
                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Your logic when OK button is clicked
                                char A[] = new char[7];
                                for(int i = 0; i < 7; i++){
                                    A[i] = '0';
                                }
                                for(int i = 0; i < itemsSelected.size(); i++){
                                    A[Integer.valueOf(itemsSelected.get(i).toString())] = '1';
                                }
                                thisSchedule.setRepeatDay(String.valueOf(A));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                Dialog dialog;
                dialog = builder.create();
                //((AlertDialog)).getListView().setItemChecked(1, true);
                dialog.show();
            }
        });
    }

    private void getSchedule(String scheduleId){
        database.child("schedules").child(scheduleId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    thisSchedule = task.getResult().getValue(Schedule.class);
                    thisSchedule.setScheduleID(scheduleId);
                    temp_data.setText(String.valueOf(thisSchedule.getTemp()));
                    humi_data.setText(String.valueOf(thisSchedule.getHumid()));
                    start_time.setText(thisSchedule.getStartTime());
                    end_time.setText(thisSchedule.getEndTime());
                }
            }
        });
    }

    private void getAndListenSchedule(String scheduleId){
        Query scheduleDb = database.child("schedules").child(scheduleId);
        scheduleDb.keepSynced(true);
        scheduleDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    thisSchedule = dataSnapshot.getValue(Schedule.class);
                    temp_data.setText(String.valueOf(thisSchedule.getTemp()));
                    humi_data.setText(String.valueOf(thisSchedule.getHumid()));
                    start_time.setText(thisSchedule.getStartTime());
                    end_time.setText(thisSchedule.getEndTime());
                }
                else
                {
                    Log.d("schedule", "Database is empty now!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateSchedule(){
        database.child("schedules").child(thisSchedule.getScheduleID()).child("temp").setValue(thisSchedule.getTemp());
        database.child("schedules").child(thisSchedule.getScheduleID()).child("humid").setValue(thisSchedule.getHumid());
        database.child("schedules").child(thisSchedule.getScheduleID()).child("startTime").setValue(thisSchedule.getStartTime());
        database.child("schedules").child(thisSchedule.getScheduleID()).child("endTime").setValue(thisSchedule.getEndTime());
        database.child("schedules").child(thisSchedule.getScheduleID()).child("repeatDay").setValue(thisSchedule.getRepeatDay());
    }

    private void setTime(int flag){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                if (flag == 0) {
                    calendar.set(0, 0, 0, hourOfDay, minute);
                    start_time.setText((simpleDateFormat.format(calendar.getTime())));
                    thisSchedule.setStartTime(simpleDateFormat.format(calendar.getTime()));
                }
                else {
                    calendar.set(0, 0, 0, hourOfDay, minute);
                    end_time.setText((simpleDateFormat.format(calendar.getTime())));
                    thisSchedule.setEndTime(simpleDateFormat.format(calendar.getTime()));
                }
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    public void changeNum(View view, int flag){
        int tempNum = thisSchedule.getTemp();
        int humiNum = thisSchedule.getHumid();
        switch (flag) {
            case 1:
                tempNum += 1;
                break;
            case 2:
                tempNum -= 1;
                break;
            case 3:
                humiNum += 1;
                break;
            case 4:
                humiNum -= 1;
                break;
        }
        if (flag < 3) {
            if (tempNum > 30) {
                tempNum = 30;
            } else if (tempNum < 16) {
                tempNum = 16;
            }
            temp_data.setText("" + tempNum);
            thisSchedule.setTemp(tempNum);
        }
        else {
            if (humiNum > 60) {
                humiNum = 60;
            } else if (humiNum < 40) {
                humiNum = 40;
            }
            humi_data.setText("" + humiNum);
            thisSchedule.setHumid(humiNum);
        }
    }
}