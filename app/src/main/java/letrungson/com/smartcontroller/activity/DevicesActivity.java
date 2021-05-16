package letrungson.com.smartcontroller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Device;
import letrungson.com.smartcontroller.service.Database;
import letrungson.com.smartcontroller.service.MQTTService;

public class DevicesActivity extends AppCompatActivity {
    MQTTService mqttService;
    private ListView listViewDevices;
    private Spinner spinnerDeviceType;
    private Database db_service;
    private FirebaseDatabase db;
    private DatabaseReference dbRefDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        mqttService = new MQTTService(this);

        //Setup database
        db_service = new Database();
        db = FirebaseDatabase.getInstance();
        dbRefDevices = db.getReference("devices");

        //Setup  Toolbar
        Toolbar toolbar = findViewById(R.id.devices_toolbar);
        toolbar.setTitle("Devices");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup Room ID
        Intent intent = getIntent();
        String roomId = intent.getStringExtra("roomId");

        //Set up Button Add
        Button btn_add = findViewById(R.id.btn_add_devices);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicesActivity.this, AddDevicesActivity.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });

        //Setup List View
        List<String> type = Arrays.asList(getResources().getStringArray(R.array.default_devices_type));
        ArrayList<DeviceAdapter> deviceAdapterArrayList = new ArrayList<DeviceAdapter>();
        for (String ty : type) {
            deviceAdapterArrayList.add(new DeviceAdapter(DevicesActivity.this, R.layout.list_devices_item, new ArrayList<Device>()));
        }
        listViewDevices = findViewById(R.id.list_devices);

        //Setup spinner
        spinnerDeviceType = findViewById(R.id.spinner_devices);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(DevicesActivity.this, R.layout.spinner_item, type);
        spinnerDeviceType.setAdapter(spinnerAdapter);
        spinnerDeviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listViewDevices.setAdapter(deviceAdapterArrayList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Reference to database child "devices" listener
        dbRefDevices.orderByChild("roomId").equalTo(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Device device = snapshot.getValue(Device.class);
                device.setDeviceId(snapshot.getKey());
                if (device.getType() != null) {
                    int currentType = 0;
                    deviceAdapterArrayList.get(0).add(device);
                    for (int i = 1; i < type.size(); i++) {
                        if (type.get(i).equals(device.getType())) {
                            deviceAdapterArrayList.get(i).add(device);
                            currentType = i;
                            break;
                        }
                    }
                    int currentViewPos = spinnerDeviceType.getSelectedItemPosition();
                    if (currentViewPos == currentType || currentViewPos == 0) {
                        listViewDevices.setAdapter(deviceAdapterArrayList.get(currentViewPos));
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Device device = snapshot.getValue(Device.class);
                String deviceID = snapshot.getKey();
                if (device.getType() != null) {
                    int currentViewPos = spinnerDeviceType.getSelectedItemPosition();
                    int currentType = 0;
                    for (int i = 1; i < type.size(); i++) {
                        if (type.get(i).equals(device.getType()))
                            currentType = i;
                    }
                    if (currentViewPos == 0 || currentViewPos == currentType) {
                        int j;
                        DeviceAdapter deviceAdapter_current = deviceAdapterArrayList.get(currentViewPos);
                        for (j = 0; j < deviceAdapter_current.getCount(); j++) {
                            if (deviceAdapter_current.getItem(j).getDeviceId().equals(deviceID)) {
                                deviceAdapter_current.getItem(j).assign(device);
                                break;
                            }
                        }
                        View convertView = listViewDevices.getChildAt(j - listViewDevices.getFirstVisiblePosition());
                        SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.device_item_switch);
                        TextView textView = (TextView) convertView.findViewById(R.id.device_item_title);
                        textView.setText(device.getDeviceName());
                        switchCompat.setChecked(device.getState().equals("On"));
                    } else {
                        DeviceAdapter deviceAdapter_current = deviceAdapterArrayList.get(currentType);
                        for (int j = 0; j < deviceAdapter_current.getCount(); j++) {
                            if (deviceAdapter_current.getItem(j).getDeviceId().equals(deviceID))
                                deviceAdapter_current.getItem(j).assign(device);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Device device = snapshot.getValue(Device.class);
                String deviceID = snapshot.getKey();
                if (device.getType() != null) {
                    int currentType = 0;
                    for (int i = 1; i < type.size(); i++) {
                        if (type.get(i).equals(device.getType()))
                            currentType = i;
                    }
                    DeviceAdapter deviceAdapter_current = deviceAdapterArrayList.get(currentType);
                    int j;
                    for (j = 0; j < deviceAdapter_current.getCount(); j++) {
                        if (deviceAdapter_current.getItem(j).getDeviceId().equals(deviceID)) {
                            Device del_device = deviceAdapter_current.getItem(j);
                            deviceAdapter_current.remove(del_device);
                            deviceAdapterArrayList.get(0).remove(del_device);
                            break;
                        }
                    }
                    int currentViewPos = spinnerDeviceType.getSelectedItemPosition();
                    if (currentViewPos == currentType) {
                        listViewDevices.setAdapter(deviceAdapter_current);
                    } else if (currentViewPos == 0) {
                        listViewDevices.setAdapter(deviceAdapterArrayList.get(0));
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_devices, menu);
        return true;
    }

    //    int index=0;
//    int index1=0;
    private class DeviceAdapter extends ArrayAdapter<Device> {
        private int layout;

        public DeviceAdapter(Context context, int resource, ArrayList<Device> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Device device = getItem(position);
            DeviceHolder mainDeviceViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                DeviceHolder deviceHolder = new DeviceHolder();

                deviceHolder.title = (TextView) convertView.findViewById(R.id.device_item_title);
                deviceHolder.title.setText(device.getDeviceName());
                deviceHolder.switchCompat = (SwitchCompat) convertView.findViewById(R.id.device_item_switch);
                if (device.getState() != null) {
                    deviceHolder.switchCompat.setChecked(device.getState().equals("On"));
                }

                deviceHolder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!buttonView.isPressed()) {
                            return;
                        }
                        String state = "Off";
                        if (isChecked) {
                            state = "On";
                        }
                        db_service.updateDevice(device.getDeviceId(), state);
                        db_service.addLog(device.getDeviceId(), state);
                        mqttService.sendDataMQTT(device.getDeviceId(), state);
                    }
                });
                /*Toast toast = Toast.makeText(DevicesActivity.this, "null" + index, Toast.LENGTH_SHORT);
                index++;
                toast.show();*/
                convertView.setTag(deviceHolder);
            } else {
                mainDeviceViewholder = (DeviceHolder) convertView.getTag();
                /*Toast toast = Toast.makeText(DevicesActivity.this, "main" + index1, Toast.LENGTH_SHORT);
                index1++;*/
            }
            return convertView;
        }

    }

    public class DeviceHolder {
        TextView title;
        SwitchCompat switchCompat;
    }


    //Spinner Adapter
    private class SpinnerAdapter extends ArrayAdapter<String> {
        private int layout;

        public SpinnerAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpinnerViewHolder mainSpinnerViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                SpinnerViewHolder spinnerViewHolder = new SpinnerViewHolder();
                String title = getItem(position);
                spinnerViewHolder.title = (TextView) convertView.findViewById(R.id.spinner_title);
                spinnerViewHolder.title.setText(title);
                spinnerViewHolder.imageView = (ImageView) convertView.findViewById(R.id.spinner_image);
                switch (position) {
                    case 0:
                        spinnerViewHolder.imageView.setImageResource(R.drawable.ic_baseline_clear_all_24);
                        break;
                    case 1:
                        spinnerViewHolder.imageView.setImageResource(R.drawable.air_conditioner);
                        break;
                    case 2:
                        spinnerViewHolder.imageView.setImageResource(R.drawable.fan);
                        break;
                    case 3:
                        spinnerViewHolder.imageView.setImageResource(R.drawable.ic_baseline_whatshot_24);
                        break;
                    default:
                        spinnerViewHolder.imageView.setImageResource(R.drawable.ic_baseline_device_unknown_24);
                        break;
                }
                convertView.setTag(spinnerViewHolder);
            } else {
                mainSpinnerViewholder = (SpinnerViewHolder) convertView.getTag();
            }
            return convertView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }

    public class SpinnerViewHolder {
        ImageView imageView;
        TextView title;
    }
}
