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
import letrungson.com.smartcontroller.model.Device;

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
/*
    public String getStateDevice(String deviceId){
        final String[] currentState = new String[1];
        mDatabase.child(deviceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Device device = dataSnapshot.getValue(Device.class);
                If(device == null){
                    updateDevice(deviceId, );
                }
                currentState[0] = device.getCurrentState();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        return currentState[0];
    }*/
/*
    public List<User> getAllUser(){
        List<User> allUsers = new ArrayList<User>();
        allUsers.clear();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    String key = data.getKey();
                    user.setId(key);
                    allUsers.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        for (User student : allUsers) {
            System.out.println(student.getUsername());
        }
        return allUsers;
    }*/


    public void addRoom(String roomName) {
        String id = "Room" + rooms.push().getKey();
        rooms.child(id).child("roomName").setValue(roomName);
    }

    public void updateRoom(String roomid, String temp) {
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
//    public List<String> getAllDevices(){
//        List<String> listDevices = new ArrayList<>();
//        Query allDevices = database.getReference("devices");
//        allDevices.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                listDevices.clear();
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    Device device = data.getValue(Device.class);
//                    String deviceId = data.getKey();
//                    device.setRoomId(deviceId);
//                    listDevices.add(device.getState());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//        return listDevices;
//    }
}
