package letrungson.com.smartcontroller.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import letrungson.com.smartcontroller.model.Data;
import letrungson.com.smartcontroller.model.LogState;
import letrungson.com.smartcontroller.model.Room;

public class Database {

    private static final String TAG = "Database";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference sensors, logs, devices, rooms;

    public Database(){
        sensors = database.getReference("sensore");
        logs = database.getReference("logs");
        devices = database.getReference("devices");
        rooms = database.getReference("rooms");
    }

    public void addSensorLog(Data o) {
        String id = "Sensor" + sensors.push().getKey();
        sensors.child(id).setValue(o);
    }

    public void addLog(String deviceid, String newState) {
        FirebaseUser user = mAuth.getCurrentUser();
        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datetime =  formatter.format(LocalDateTime.now());
        String userid = user.getUid();
        LogState log = new LogState(datetime, deviceid, newState, userid);
        String id = "Log" + logs.push().getKey();
        logs.child(id).setValue(log);
    }

    public void addRoom(String roomName) {
        String id = "Room" + rooms.push().getKey();
        rooms.child(id).child("roomName").setValue(roomName);
    }

    public void updateRoomTemp(String roomid, String temp) {
        rooms.child(roomid).child("roomTemp").setValue(temp);
    }

    public void updateDevice(String deviceId, String deviceName, String currentState) {
        devices.child(deviceId).child("deviceName").setValue(deviceName);
        devices.child(deviceId).child("State").setValue(currentState);
    }

    public void updateDevice(String deviceId, String currentState) {
        devices.child(deviceId).child("State").setValue(currentState);
    }

    public List<Room> getAllRoom(){
        List<Room> listRoom = new ArrayList<>();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return listRoom;
    }
}
