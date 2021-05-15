package letrungson.com.smartcontroller.service;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

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
import java.util.HashMap;
import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.activity.DevicesActivity;
import letrungson.com.smartcontroller.model.Data;
import letrungson.com.smartcontroller.model.LogState;
import letrungson.com.smartcontroller.model.Room;
import letrungson.com.smartcontroller.model.Device;

public class Database {

    private static final String TAG = "Database";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference sensors, logs, devices, rooms;

    public Database() {
        sensors = database.getReference("sensors");
        logs = database.getReference("logs");
        devices = database.getReference("devices");
        rooms = database.getReference("rooms");
    }

    public void addSensorLog(Data o) {
        String id = "Sensor" + sensors.push().getKey();
        sensors.child(id).setValue(o);
    }

    public void addLog(String deviceid, String newState, String userId) {
        FirebaseUser user = mAuth.getCurrentUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datetime = formatter.format(LocalDateTime.now());
        LogState log = new LogState(datetime, deviceid, newState, userId);
        String id = "Log" + logs.push().getKey();
        logs.child(id).setValue(log);
    }

    public void addLog(String deviceid, String newState) {
        FirebaseUser user = mAuth.getCurrentUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datetime = formatter.format(LocalDateTime.now());
        String userid = user.getUid();
        LogState log = new LogState(datetime, deviceid, newState, userid);
        String id = "Log" + logs.push().getKey();
        logs.child(id).setValue(log);
    }

    public void addRoom(String roomName) {
        String id = "Room" + rooms.push().getKey();
        rooms.child(id).child("roomName").setValue(roomName);
    }

    public void updateRoom(String roomId, String temp, String humid) {
        rooms.child(roomId).child("roomCurrentTemp").setValue(temp);
        rooms.child(roomId).child("roomCurrentHumidity").setValue(humid);
    }

    public void removLog(){
        logs.removeValue();
        Log.d("db", "removed successfully");
    }

    public void addDevice(String deviceName, String roomId, String type) {
        String id = "Device" + rooms.push().getKey();
        HashMap hashMap = new HashMap();
        hashMap.put("deviceName",deviceName);
        hashMap.put("roomId",roomId);
        hashMap.put("state","Off");
        hashMap.put("type",type);
        hashMap.put("deviceName",deviceName);
        devices.child(id).setValue(hashMap);
    }

    public void updateDevice(String deviceId, String currentState) {
        devices.child(deviceId).child("state").setValue(currentState);
    }

}
