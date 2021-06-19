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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Device;
import letrungson.com.smartcontroller.model.Schedule;
import letrungson.com.smartcontroller.tools.Check;
import letrungson.com.smartcontroller.tools.Transform;

public class ScheduleSetActivity extends AppCompatActivity {
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    Schedule thisSchedule;
    String scheduleId;
    String roomId;
    TextView start_time, end_time, temp_data, humid_data, repeat_day_text, device_text, title;
    ImageButton up_temp_btn, down_temp_btn, up_humid_btn, down_humid_btn, close_btn, tick_btn;
    Button delete_btn;
    List<Device> listDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        getAllDevicesInRoom(roomId);

        setContentView(R.layout.activity_editschedule);
        title = (TextView) findViewById(R.id.title);
        title.setText("Add Schedule");
        start_time = (TextView) findViewById(R.id.start_time_text);
        end_time = (TextView) findViewById(R.id.end_time_text);
        up_temp_btn = (ImageButton) findViewById(R.id.up_temp_btn);
        down_temp_btn = (ImageButton) findViewById(R.id.down_temp_btn);
        up_humid_btn = (ImageButton) findViewById(R.id.up_humid_btn);
        down_humid_btn = (ImageButton) findViewById(R.id.down_humid_btn);
        temp_data = (TextView) findViewById(R.id.temp_data_view);
        humid_data = (TextView) findViewById(R.id.humid_data_view);
        close_btn = (ImageButton) findViewById(R.id.close_btn);
        tick_btn = (ImageButton) findViewById(R.id.tick_btn);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        delete_btn.setVisibility(View.GONE);
        repeat_day_text = (TextView) findViewById(R.id.repeat_day_text);
        device_text = (TextView) findViewById(R.id.device_text);

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

        repeat_day_text.setOnClickListener(new View.OnClickListener() {
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
                dialog.show();
            }
        });

        // lines below is prepare to set repeat day
        AlertDialog.Builder deviceBuilder = new AlertDialog.Builder(this);

        device_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> listDeviceName = listDevice.stream()
                        .map(Device::getDeviceName)
                        .collect(Collectors.toList());
                if (listDeviceName.size() == 0) {
                    deviceBuilder.setTitle("Choose device:");
                    deviceBuilder.setMessage("No device in this room!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    thisSchedule.setListDevice(null);
                                }
                            });

                } else {
                    final ArrayList itemsSelected = new ArrayList();
                    boolean yetChecked[] = new boolean[listDevice.size()];
                    for (int i = 0; i < listDevice.size(); i++) {
                        if (Check.checkExistDeviceId(thisSchedule.getListDevice(), listDevice.get(i).getDeviceId())) {
                            yetChecked[i] = true;
                            itemsSelected.add(i);
                        } else {
                            yetChecked[i] = false;
                        }
                    }
                    deviceBuilder.setTitle("Choose device:");
                    deviceBuilder.setMultiChoiceItems(listDeviceName.toArray(new String[listDevice.size()]), yetChecked,
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
                                    List<String> listId = new ArrayList<String>();
                                    for (int i = 0; i < itemsSelected.size(); i++) {
                                        listId.add(listDevice.get((Integer) itemsSelected.get(i)).getDeviceId());
                                    }
                                    thisSchedule.setListDevice(listId);
                                    device_text.setText(Transform.toListNameFromDeviceId(listDevice, thisSchedule.getListDevice()));
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                }
                Dialog dialog;
                dialog = deviceBuilder.create();
                //((AlertDialog)).getListView().setItemChecked(1, true);
                dialog.show();

            }
        });
    }

    public void getAllDevicesInRoom(String roomId) {
        listDevice = new ArrayList<Device>();
        Query allDevice = database.child("devices").orderByChild("roomId").equalTo(roomId);
        allDevice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listDevice.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Device device = data.getValue(Device.class);
                    if (!device.getType().equals("Sensor")) {
                        String deviceId = data.getKey();
                        device.setDeviceId(deviceId);
                        listDevice.add(device);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setSchedule() {
        thisSchedule = new Schedule();
        thisSchedule.setRoomId(roomId);
        temp_data.setText(String.valueOf(thisSchedule.getTemp()));
        humid_data.setText(String.valueOf(thisSchedule.getHumid()));
        start_time.setText(thisSchedule.getStartTime());
        end_time.setText(thisSchedule.getEndTime());
        repeat_day_text.setText(Transform.BinaryToDaily(thisSchedule.getRepeatDay()));
        device_text.setText(Transform.toListNameFromDeviceId(listDevice, thisSchedule.getListDevice()));
    }

    private void addSchedule() {
        scheduleId = "Schedule" + database.child("schedules").push().getKey();
        database.child("schedules").child(scheduleId).setValue(thisSchedule);
        thisSchedule.setScheduleId(scheduleId);
    }

    private void setTime(int flag) {
        Calendar calendar = Calendar.getInstance();
        int previous_hour, previous_minute;
        if (flag == 0) {
            previous_hour = calendar.get(Calendar.HOUR_OF_DAY);
            previous_minute = calendar.get(Calendar.MINUTE);
        } else {
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
