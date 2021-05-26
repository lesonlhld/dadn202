package letrungson.com.smartcontroller.activity;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Schedule;
import letrungson.com.smartcontroller.tools.Transform;

public class ScheduleEditActivity extends AppCompatActivity {
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    Schedule thisSchedule;
    String scheduleId;
    TextView start_time, end_time, temp_data, humid_data, repeat_day_text;
    ImageButton up_temp_btn, down_temp_btn, up_humid_btn, down_humid_btn, close_btn, tick_btn;
    Button set_repeat_day_btn, delete_btn;

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
        up_humid_btn = (ImageButton) findViewById(R.id.up_humi_btn);
        down_humid_btn = (ImageButton) findViewById(R.id.down_humi_btn);
        temp_data = (TextView) findViewById(R.id.temp_data_view);
        humid_data = (TextView) findViewById(R.id.humi_data_view);
        close_btn = (ImageButton) findViewById(R.id.close_btn);
        tick_btn = (ImageButton) findViewById(R.id.tick_btn);
        set_repeat_day_btn = (Button) findViewById(R.id.set_repeat_day_btn);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        repeat_day_text = (TextView) findViewById(R.id.repeat_day_text);

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

        up_humid_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 3;
                changeNum(v, flag);
            }
        });

        down_humid_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 4;
                changeNum(v, flag);
            }
        });


        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(ScheduleEditActivity.this)
                        .setTitle("Delete")
                        .setMessage("Do you want to delete this schedule?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                deleteSchedule();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
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

        set_repeat_day_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                final ArrayList itemsSelected = new ArrayList();
                boolean yetChecked[] = new boolean[7];
                for (int i = 0; i < 7; i++) {
                    if (thisSchedule.getRepeatDay().toCharArray()[i] == '1') {
                        yetChecked[i] = true;
                        itemsSelected.add(i);
                    } else {
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
                                for (int i = 0; i < 7; i++) {
                                    A[i] = '0';
                                }
                                for (int i = 0; i < itemsSelected.size(); i++) {
                                    A[Integer.valueOf(itemsSelected.get(i).toString())] = '1';
                                }
                                thisSchedule.setRepeatDay(String.valueOf(A));
                                repeat_day_text.setText(Transform.BinaryToDaily(String.valueOf(A)));
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

    private void getSchedule(String scheduleId) {
        database.child("schedules").child(scheduleId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    thisSchedule = task.getResult().getValue(Schedule.class);
                    thisSchedule.setScheduleId(scheduleId);
                    temp_data.setText(String.valueOf(thisSchedule.getTemp()));
                    humid_data.setText(String.valueOf(thisSchedule.getHumid()));
                    start_time.setText(thisSchedule.getStartTime());
                    end_time.setText(thisSchedule.getEndTime());
                    repeat_day_text.setText(Transform.BinaryToDaily(thisSchedule.getRepeatDay()));
                }
            }
        });
    }

    private void getAndListenSchedule(String scheduleId) {
        Query scheduleDb = database.child("schedules").child(scheduleId);
        scheduleDb.keepSynced(true);
        scheduleDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    thisSchedule = dataSnapshot.getValue(Schedule.class);
                    temp_data.setText(String.valueOf(thisSchedule.getTemp()));
                    humid_data.setText(String.valueOf(thisSchedule.getHumid()));
                    start_time.setText(thisSchedule.getStartTime());
                    end_time.setText(thisSchedule.getEndTime());
                } else {
                    Log.d("schedule", "Database is empty now!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateSchedule() {
        database.child("schedules").child(thisSchedule.getScheduleId()).child("temp").setValue(thisSchedule.getTemp());
        database.child("schedules").child(thisSchedule.getScheduleId()).child("humid").setValue(thisSchedule.getHumid());
        database.child("schedules").child(thisSchedule.getScheduleId()).child("startTime").setValue(thisSchedule.getStartTime());
        database.child("schedules").child(thisSchedule.getScheduleId()).child("endTime").setValue(thisSchedule.getEndTime());
        database.child("schedules").child(thisSchedule.getScheduleId()).child("repeatDay").setValue(thisSchedule.getRepeatDay());
    }

    private void deleteSchedule() {
        database.child("schedules").child(thisSchedule.getScheduleId()).removeValue();
    }

    private void setTime(int flag) {
        Calendar calendar = Calendar.getInstance();
        int previous_hour, previous_minute;
        if (flag == 0) {
            previous_hour = Integer.parseInt(thisSchedule.getStartTime().substring(0, 2));
            previous_minute = Integer.parseInt(thisSchedule.getStartTime().substring(3, 5));
        } else {
            previous_hour = Integer.parseInt(thisSchedule.getEndTime().substring(0, 2));
            previous_minute = Integer.parseInt(thisSchedule.getEndTime().substring(3, 5));
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                if (flag == 0) {
                    calendar.set(0, 0, 0, hour, minute);
                    start_time.setText((simpleDateFormat.format(calendar.getTime())));
                    thisSchedule.setStartTime(simpleDateFormat.format(calendar.getTime()));
                } else {
                    calendar.set(0, 0, 0, hour, minute);
                    end_time.setText((simpleDateFormat.format(calendar.getTime())));
                    thisSchedule.setEndTime(simpleDateFormat.format(calendar.getTime()));
                }
            }
        }, previous_hour, previous_minute, true);
        timePickerDialog.show();
    }

    public void changeNum(View view, int flag) {
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
            temp_data.setText(String.valueOf(tempNum));
            thisSchedule.setTemp(tempNum);
        } else {
            if (humidNum > 60) {
                humidNum = 60;
            } else if (humidNum < 40) {
                humidNum = 40;
            }
            humid_data.setText(String.valueOf(humidNum));
            thisSchedule.setHumid(humidNum);
        }
    }
}