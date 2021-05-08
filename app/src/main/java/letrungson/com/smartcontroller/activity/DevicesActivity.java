package letrungson.com.smartcontroller.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Device;
import letrungson.com.smartcontroller.model.Room;
import letrungson.com.smartcontroller.service.Database;
import java.util.ArrayList;
import java.util.List;

public class DevicesActivity extends AppCompatActivity {
    private ListView list_view_air_conditioners;
    private List list_air_conditioners ;

    private ListView list_view_fans;
    private List list_fans;

    private FirebaseDatabase db;
    private DatabaseReference dbRefDevices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        //Setup database
        db = FirebaseDatabase.getInstance();
        dbRefDevices=db.getReference("devices");
        //Setup  Toolbar
        Toolbar toolbar=findViewById(R.id.devices_toolbar);
        toolbar.setTitle("Devices");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup RoomName
        Intent intent=getIntent();
        String roomName=intent.getStringExtra("roomName");
        roomName="Living Room";
        TextView header_room_name=findViewById(R.id.header_room_name);
        header_room_name.setText(roomName);


        //Set up Button Air Conditioner and Fan
        Button btn_fan = findViewById(R.id.btn_fan);
        Button btn_air_conditioner= findViewById(R.id.btn_air_conditioner);


        //Setup List View
        list_view_air_conditioners=findViewById(R.id.list_air_conditioners);
        list_air_conditioners = new ArrayList<ArrayList>();
        CustomAdapter customAdapterAC = new CustomAdapter(this,R.layout.list_devices_item,list_air_conditioners);
        list_view_air_conditioners.setAdapter(customAdapterAC);

        list_view_fans =findViewById(R.id.list_fans);
        List list_fans =new ArrayList();
        CustomAdapter customAdapterFan = new CustomAdapter(this,R.layout.list_devices_item,list_fans);
        list_view_fans.setAdapter(customAdapterFan);


        dbRefDevices.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
                Device device=snapshot.getValue(Device.class);
                device.setDeviceId(snapshot.getKey());
                if (device.getType()!=null){
                    if(device.getType().equals("air_conditioner")){
                        list_air_conditioners.add(device);
                        customAdapterAC.notifyDataSetChanged();
                    }
                    else if (device.getType().equals("fan")){
                        list_fans.add(device);
                        customAdapterFan.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });







        //Test
//        for (int i=5;i<8;i++) {
//            Device device= new Device();
//            String deviceID = "Device"+ dbRefDevices.push().getKey();
//            device.setDeviceName("Fan "+ String.valueOf(i));
//            device.setState("Off");
//            device.setType("fan");
//            device.setRoomId("Room3");
//            dbRefDevices.child(deviceID).setValue(device);
//        }
//        for (int i=5;i<8;i++) {
//            Device device= new Device();
//            String deviceID = "Device"+ dbRefDevices.push().getKey();
//            device.setDeviceName("Air conditioner  "+ String.valueOf(i));
//            device.setState("Off");
//            device.setType("fan");
//            device.setRoomId("Room3");
//            dbRefDevices.child(deviceID).setValue(device);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private class CustomAdapter extends ArrayAdapter<Device>{
        private int layout;

        public CustomAdapter( Context context, int resource,  List<Device> objects) {
            super(context, resource, objects);
            layout=resource;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder=null;
            if (convertView==null){
                LayoutInflater inflater =LayoutInflater.from(getContext());
                convertView=inflater.inflate(layout,parent, false);
                ViewHolder viewHolder=new ViewHolder();
                Device device = getItem(position);
                viewHolder.title=(TextView) convertView.findViewById(R.id.list_ac_item_text);
                viewHolder.title.setText(device.getDeviceName());
                viewHolder.switchCompat=(SwitchCompat) convertView.findViewById(R.id.list_ac_item_btn);
                if (device.getState()!=null){
                   viewHolder.switchCompat.setChecked(device.getState().equals("On"));

                }
                viewHolder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            dbRefDevices.child(device.getDeviceId()).child("state").setValue("On");
                        }
                        else {
                            dbRefDevices.child(device.getDeviceId()).child("state").setValue("Off");
                        }
                    }
                });

                convertView.setTag(viewHolder);
            }
            else {
                mainViewholder=(ViewHolder) convertView.getTag();
            }
            return convertView;
        }

    }
    public class ViewHolder{
        TextView title;
        SwitchCompat switchCompat;
    }
}
