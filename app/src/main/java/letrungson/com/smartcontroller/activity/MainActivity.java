package letrungson.com.smartcontroller.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.util.ArrayList;
import java.util.List;

import letrungson.com.smartcontroller.BuildConfig;
import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.adapter.RoomViewAdapter;
import letrungson.com.smartcontroller.adapter.SpacingItemDecorator;
import letrungson.com.smartcontroller.model.Device;
import letrungson.com.smartcontroller.model.Room;
import letrungson.com.smartcontroller.service.Database;

//import es.rcti.printerplus.printcom.models.PrintTool;
//import es.rcti.printerplus.printcom.models.StructReport;

//https://github.com/rcties/PrinterPlusCOMM
//https://github.com/mik3y/usb-serial-for-android
public class MainActivity extends AppCompatActivity implements SerialInputOutputManager.Listener {
    private static final String ACTION_USB_PERMISSION = "com.android.recipes.USB_PERMISSION";
    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    public static List<Device> allDevices;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    UsbSerialPort port;
    Button home, more;
    ImageButton room_btn;
    RelativeLayout powerAllRooms;
    TextView power_state;
    RoomViewAdapter roomViewAdapter;
    private List<Room> listRoom;
    private RecyclerView recyclerView;
    private Thread readThread;

    //private UsbHidDevice device = null;
    private boolean mRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAllRoom();
        setContentView(R.layout.homescreeen);

        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        more = findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MoreActivity.class));
                finish();
            }
        });


        recyclerView = findViewById(R.id.gridView);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        roomViewAdapter = new RoomViewAdapter(MainActivity.this, listRoom);
        recyclerView.setAdapter(roomViewAdapter);

        room_btn = findViewById(R.id.room_manage_btn);
        room_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RoomActivity.class));
            }
        });

        //Setup power all room:
        powerAllRooms = (RelativeLayout) findViewById(R.id.power);
        power_state = (TextView) findViewById(R.id.power_state);
        allDevices = new ArrayList<Device>();
        Query refRoomDevices = FirebaseDatabase.getInstance().getReference("devices");
        refRoomDevices.addChildEventListener(new ChildEventListener() {
            private int countDeviceOn = 0;

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Device device = snapshot.getValue(Device.class);
                device.setDeviceId(snapshot.getKey());
                if (!device.getType().equals("Sensor")) {
                    if (device.getState().equals("1")) countDeviceOn++;
                    if (countDeviceOn == 0) {
                        power_state.setText("Turn On");
                    } else if (countDeviceOn == 1) {
                        power_state.setText("Turn Off");
                    }
                }
                allDevices.add(device);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, @Nullable String previousChildName) {
                Device device = snapshot.getValue(Device.class);
                String deviceID = snapshot.getKey();
                if (!device.getType().equals("Sensor")) {
                    for (Device device0 : allDevices) {
                        if (device0.getDeviceId().equals(deviceID)) {
                            if (device.getState().equals("1") && device0.getState().equals("0"))
                                countDeviceOn++;
                            else if (device.getState().equals("0") && device0.getState().equals("1"))
                                countDeviceOn--;
                            device0.assign(device);
                            break;
                        }
                    }
                    if (countDeviceOn == 0) {
                        power_state.setText("Turn On");
                    } else if (countDeviceOn == 1) {
                        power_state.setText("Turn Off");
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Device device = snapshot.getValue(Device.class);
                String deviceID = snapshot.getKey();
                if (!device.getType().equals("Sensor")) {
                    for (Device device0 : allDevices) {
                        if (device0.getDeviceId().equals(deviceID)) {
                            if (device0.getState().equals("1"))
                                countDeviceOn--;
                            allDevices.remove(device0);
                            break;
                        }
                    }
                    if (countDeviceOn == 0) {
                        power_state.setText("Turn On");
                    } else if (countDeviceOn == 1) {
                        power_state.setText("Turn Off");
                    }
                } else {
                    for (Device device0 : allDevices) {
                        if (device0.getDeviceId().equals(deviceID)) {
                            allDevices.remove(device0);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        powerAllRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allDevices.size() > 0) {
                    String newState = "0";
                    if (power_state.getText().equals("Turn On"))
                        newState = "1";
                    for (Device device : allDevices) {
                        if (!device.getType().equals("Sensor") && !device.getState().equals(newState)) {
                            Database.updateDevice(device.getDeviceId(), newState);
                            Database.addLog(device.getDeviceId(), newState);
                        }
                    }
                }
            }
        });

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

/*
        if (availableDrivers.isEmpty()) {
            Log.d("UART", "UART is not available");
            txtOut.setText("UART is not available");
        }else {
            Log.d("UART", "UART is available");
            txtOut.setText("UART is available");

            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                manager.requestPermission(driver.getDevice(), usbPermissionIntent);
                manager.requestPermission(driver.getDevice(), PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));
            } else {
                // Most devices have just one port (port 0)
                port = driver.getPorts().get(0);
                try {
                    port.open(connection);
                    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                    port.write("hello abc#".getBytes(), 1000);
                    txtOut.setText("A sample string is sent");

                    SerialInputOutputManager usbIoManager = new SerialInputOutputManager(port, this);
                    Executors.newSingleThreadExecutor().submit(usbIoManager);
                } catch (Exception e) {
                    txtOut.setText("Sending a message is fail");
                }

            }
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            port.close();
        } catch (Exception e) {

        }
    }

    @Override
    public void onNewData(final byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //temperature.append(Arrays.toString(data));
            }
        });
    }

    @Override
    public void onRunError(Exception e) {

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Do you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
                            finishAffinity();
                        } else if (Build.VERSION.SDK_INT >= 21) {
                            finishAndRemoveTask();
                        }
                        System.exit(0);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void getAllRoom() {
        listRoom = new ArrayList<>();
        Query allRoom = database.getReference("rooms");
        allRoom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listRoom.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Room room = data.getValue(Room.class);
                    String roomId = data.getKey();
                    room.setRoomId(roomId);
                    listRoom.add(room);
                }
                roomViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

