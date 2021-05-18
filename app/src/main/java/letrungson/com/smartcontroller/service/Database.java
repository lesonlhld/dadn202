package letrungson.com.smartcontroller.service;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import letrungson.com.smartcontroller.model.Data;
import letrungson.com.smartcontroller.model.LogState;

public class Database {
    private static final String TAG = "Database";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference sensors, logs, devices, rooms, schedules;

    public Database() {
        sensors = database.getReference("sensors");
        logs = database.getReference("logs");
        devices = database.getReference("devices");
        rooms = database.getReference("rooms");
        schedules = database.getReference("schedules");
    }

    public void addSensorLog(Data o) {
        String id = "Sensor" + sensors.push().getKey();
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("deviceId", o.getKey());
        hashMap.put("last_value", o.getLast_value());
        hashMap.put("updated_at", o.getUpdated_at().substring(0, o.getUpdated_at().length() - 4));
        sensors.child(id).setValue(hashMap);
    }

    public void addLog(String deviceId, String newState, String userId) {
        FirebaseUser user = mAuth.getCurrentUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datetime = formatter.format(LocalDateTime.now());
        LogState log = new LogState(datetime, deviceId, newState, userId);
        String id = "Log" + logs.push().getKey();
        logs.child(id).setValue(log);
    }

    public void addLog(String deviceId, String newState) {
        FirebaseUser user = mAuth.getCurrentUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datetime = formatter.format(LocalDateTime.now());
        String userId = user.getUid();
        LogState log = new LogState(datetime, deviceId, newState, userId);
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

    public void removLog() {
        logs.removeValue();
        Log.d("db", "removed successfully");
    }

    public void addDevice(String deviceId, String deviceName, String type, String roomId) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("deviceName", deviceName);
        hashMap.put("roomId", roomId);
        if (type.equals("Sensor")) hashMap.put("state", "0-0");
        else hashMap.put("state", "0");
        hashMap.put("type", type);
        devices.child(deviceId).setValue(hashMap);
    }

    public void removeDevice(String deviceId) {
        devices.child(deviceId).removeValue();
    }

    public void updateDevice(String deviceId, String currentState) {
        devices.child(deviceId).child("state").setValue(currentState);
    }

}
