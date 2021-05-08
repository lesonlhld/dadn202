package letrungson.com.smartcontroller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.adapter.CustomAdapter;
import letrungson.com.smartcontroller.model.Device;

import java.util.ArrayList;
import java.util.List;

public class DevicesActivity extends AppCompatActivity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    CustomAdapter customAdapterAC;
    CustomAdapter customAdapterFan;
    private ListView list_view_air_conditioners;
    private List list_air_conditioners;
    private ListView list_view_fans;
    private List list_fans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        //Setup database
        //Setup  Toolbar
        Toolbar toolbar = findViewById(R.id.devices_toolbar);
        toolbar.setTitle("Devices");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup RoomName
        Intent intent = getIntent();
        String roomName = intent.getStringExtra("roomName");
        String roomId = intent.getStringExtra("roomId");
        TextView header_room_name = findViewById(R.id.header_room_name);
        header_room_name.setText(roomName);


        //Set up Button Air Conditioner and Fan
        Button btn_fan = findViewById(R.id.btn_fan);
        Button btn_air_conditioner = findViewById(R.id.btn_air_conditioner);


        getAllDeviceInRoom(roomId);
        //Setup List View
        list_view_air_conditioners = findViewById(R.id.list_air_conditioners);
        customAdapterAC = new CustomAdapter(this, R.layout.list_devices_item, list_air_conditioners);
        list_view_air_conditioners.setAdapter(customAdapterAC);

        list_view_fans = findViewById(R.id.list_fans);
        customAdapterFan = new CustomAdapter(this, R.layout.list_devices_item, list_fans);
        list_view_fans.setAdapter(customAdapterFan);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void getAllDeviceInRoom(String roomId) {
        list_air_conditioners = new ArrayList<>();
        list_fans = new ArrayList<>();
        Query roomDb = database.getReference("devices");
        roomDb.keepSynced(true);
        roomDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_air_conditioners.clear();
                list_fans.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Device device = data.getValue(Device.class);
                    device.setDeviceId(data.getKey());
                    if (device.getType().equals("air_conditioner")) {
                        list_air_conditioners.add(device);
                    } else if (device.getType().equals("fan")) {
                        list_fans.add(device);
                    }
                }
                customAdapterAC.notifyDataSetChanged();
                customAdapterFan.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
