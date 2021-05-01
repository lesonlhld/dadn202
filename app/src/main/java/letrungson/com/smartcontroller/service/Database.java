package letrungson.com.smartcontroller.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import letrungson.com.smartcontroller.model.Data;
import letrungson.com.smartcontroller.model.Device;
import letrungson.com.smartcontroller.model.LogState;
import letrungson.com.smartcontroller.model.Room;

public class Database {

    private static final String TAG = "Database";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;
    public Database(String path){
        mDatabase = database.getReference(path);
    }

    public void addSensorLog(Data o, String roomid) {
        String id = "Sensor" + mDatabase.push().getKey();
        mDatabase.child(id).setValue(o);
        mDatabase.child(id).child("roomid").setValue(roomid);
    }

    public void addLog(String deviceid, String newState) {
        FirebaseUser user = mAuth.getCurrentUser();
        DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datetime =  formatter.format(LocalDateTime.now());
        String userid = user.getUid();
        LogState log = new LogState(datetime, deviceid, newState, userid);
        String id = "Log" + mDatabase.push().getKey();
        mDatabase.child(id).setValue(log);
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


    public void addRoom(String roomid, String roomName) {
        mDatabase.child(roomid).child("roomName").setValue(roomName);
    }

    public void updateRoom(String roomid, String temp) {
        mDatabase.child(roomid).child("roomTemp").setValue(temp);
    }

    public void updateDevice(String deviceId, String deviceName, String currentState) {
        mDatabase.child(deviceId).child("deviceName").setValue(deviceName);
        mDatabase.child(deviceId).child("State").setValue(currentState);
    }

    public List<Room> getAllRoom(){
        List<Room> allRooms = new ArrayList<>();
        allRooms.clear();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allRooms.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Room room = data.getValue(Room.class);
                    String roomId = data.getKey();
                    room.setRoomId(roomId);
                    allRooms.add(room);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return allRooms;
    }
}
