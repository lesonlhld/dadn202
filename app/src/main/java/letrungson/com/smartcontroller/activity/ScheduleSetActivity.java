package letrungson.com.smartcontroller.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Schedule;
import letrungson.com.smartcontroller.tools.Tranform;

public class ScheduleSetActivity extends AppCompatActivity {
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    Schedule thisSchedule;
    String scheduleID;
    String roomID;
    TextView start_time, end_time, temp_data, humi_data, repeat_day_text;
    ImageButton up_temp_btn, down_temp_btn, up_humi_btn, down_humi_btn, close_btn, tick_btn;
    Button set_repeat_day_btn, delete_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        roomID = intent.getStringExtra("roomID");

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
        set_repeat_day_btn = (Button) findViewById(R.id.set_repeat_day_btn);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        delete_btn.setVisibility(View.GONE);
        repeat_day_text = (TextView) findViewById(R.id.repeat_day_text);

        setSchedule();

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
                addSchedule();
                finish();
            }
        });
        // lines below is prepare to set repeat day
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        set_repeat_day_btn.setOnClickListener(new View.OnClickListener() {
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
                                repeat_day_text.setText(Tranform.BinaryToDaily(String.valueOf(A)));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                Dialog dialog;
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void setSchedule(){
        thisSchedule = new Schedule();
        thisSchedule.setRoomID(roomID);
        temp_data.setText(String.valueOf(thisSchedule.getTemp()));
        humi_data.setText(String.valueOf(thisSchedule.getHumid()));
        start_time.setText(thisSchedule.getStartTime());
        end_time.setText(thisSchedule.getEndTime());
        repeat_day_text.setText(Tranform.BinaryToDaily(thisSchedule.getRepeatDay()));
    }

    private void addSchedule(){
        scheduleID = "Schedule" + database.child("schedules").push().getKey();
        thisSchedule.setScheduleID(scheduleID);
        database.child("schedules").child(scheduleID).child("roomID").setValue(thisSchedule.getRoomID());
        database.child("schedules").child(scheduleID).child("temp").setValue(thisSchedule.getTemp());
        database.child("schedules").child(scheduleID).child("humid").setValue(thisSchedule.getHumid());
        database.child("schedules").child(scheduleID).child("startTime").setValue(thisSchedule.getStartTime());
        database.child("schedules").child(scheduleID).child("endTime").setValue(thisSchedule.getEndTime());
        database.child("schedules").child(scheduleID).child("repeatDay").setValue(thisSchedule.getRepeatDay());
    }


    private void setTime(int flag){
        Calendar calendar = Calendar.getInstance();
        int previous_hour, previous_minute;
        if (flag == 0) {
            previous_hour = calendar.get(Calendar.HOUR_OF_DAY);
            previous_minute = calendar.get(Calendar.MINUTE);
        }
        else {
            previous_hour = calendar.get(Calendar.HOUR_OF_DAY) + 1;
            previous_minute = calendar.get(Calendar.MINUTE);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                if (flag == 0) {
                    calendar.set(0, 0, 0, hour, minute);
                    start_time.setText((simpleDateFormat.format(calendar.getTime())));
                    thisSchedule.setStartTime(simpleDateFormat.format(calendar.getTime()));
                }
                else {
                    calendar.set(0, 0, 0, hour, minute);
                    end_time.setText((simpleDateFormat.format(calendar.getTime())));
                    thisSchedule.setEndTime(simpleDateFormat.format(calendar.getTime()));
                }
            }
        }, previous_hour, previous_minute, true);
        timePickerDialog.show();
    }

    public void changeNum(View view, int flag){
        int tempNum = thisSchedule.getTemp();
        int humidNum = thisSchedule.getHumid();
        switch (flag) {
            case 1:
                tempNum += 1;
                break;
            case 2:
                tempNum -= 1;
                break;
            case 3:
                humidNum += 1;
                break;
            case 4:
                humidNum -= 1;
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
            if (humidNum > 60) {
                humidNum = 60;
            } else if (humidNum < 40) {
                humidNum = 40;
            }
            humi_data.setText("" + humidNum);
            thisSchedule.setHumid(humidNum);
        }
    }
}
