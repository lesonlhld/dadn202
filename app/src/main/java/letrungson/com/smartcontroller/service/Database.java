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
import java.util.HashMap;

import letrungson.com.smartcontroller.model.Data;
import letrungson.com.smartcontroller.model.Device;
import letrungson.com.smartcontroller.model.LogState;
import letrungson.com.smartcontroller.model.Value;
import letrungson.com.smartcontroller.tools.Check;
import letrungson.com.smartcontroller.util.Constant;

import static letrungson.com.smartcontroller.tools.Transform.convertToCurrentTimeZone;

public class Database {
    private static final String TAG = "Database";
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference sensors = database.getReference("sensors");
    private static final DatabaseReference logs = database.getReference("logs");
    private static final DatabaseReference devices = database.getReference("devices");
    ;
    private static final DatabaseReference rooms = database.getReference("rooms");
    private static final DatabaseReference schedules = database.getReference("schedules");
    private static final DatabaseReference servers = database.getReference("servers");

    public static void addSensorLog(Data data, Value value) {
        String id = "Sensor" + sensors.push().getKey();
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("deviceId", data.getKey());
        hashMap.put("last_value", value.getData());
        hashMap.put("updated_at", convertToCurrentTimeZone(data.getUpdated_at().substring(0, data.getUpdated_at().length() - 4)));
        sensors.child(id).setValue(hashMap);
    }

    public static void addLog(String deviceId, String newState, String userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datetime = formatter.format(LocalDateTime.now());
        LogState log = new LogState(datetime, deviceId, newState, userId);
        String id = "Log" + logs.push().getKey();
        logs.child(id).setValue(log);
    }

    public static void addLog(String deviceId, String newState) {
        FirebaseUser user = mAuth.getCurrentUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datetime = formatter.format(LocalDateTime.now());
        String userId = user.getUid();
        LogState log = new LogState(datetime, deviceId, newState, userId);
        String id = "Log" + logs.push().getKey();
        logs.child(id).setValue(log);
    }

    public static void addRoom(String roomName) {
        String id = "Room" + rooms.push().getKey();
        rooms.child(id).child("roomName").setValue(roomName);
    }

    public static void updateRoom(String roomId, String temp, String humid) {
        rooms.child(roomId).child("roomCurrentTemp").setValue(temp);
        rooms.child(roomId).child("roomCurrentHumidity").setValue(humid);
    }

    public static void removLog() {
        logs.removeValue();
        Log.d("db", "removed successfully");
    }

    public static void addDevice(String deviceId, String deviceName, String type, String roomId) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("deviceName", deviceName);
        hashMap.put("roomId", roomId);
        if (type.equals("Sensor")) hashMap.put("state", "0-0");
        else hashMap.put("state", "0");
        hashMap.put("type", type);
        hashMap.put("server", Check.checkAndGetServerNameOfDevice(deviceId));
        devices.child(deviceId).setValue(hashMap);
    }

    public static void removeDevice(String deviceId) {
        devices.child(deviceId).removeValue();
    }

    public static void updateDevice(String deviceId, String currentState) {
        devices.child(deviceId).child("state").setValue(currentState);
    }

    public static void processDataMQTT(String deviceId, Data dataMqtt, Value value) {
        Query device = database.getReference("devices").child(deviceId);
        device.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Device cDevice = dataSnapshot.getValue(Device.class);
                if (cDevice.getType() != null) {
                    if (cDevice.getType().equals("Sensor")) {
                        Database.addSensorLog(dataMqtt, value);
                        String roomId = cDevice.getRoomId();
                        String data = value.getData();
                        String temp = data.substring(0, data.lastIndexOf('-')).trim();
                        String humid = data.substring(data.lastIndexOf('-') + 1).trim();
                        Database.updateRoom(roomId, temp, humid);
                    } else {//Others devices
                        //db.addLog(dataMqtt.getId(), dataMqtt.getLast_value(), "Auto");
                    }
                    //Update all device
                    Database.updateDevice(deviceId, value.getData());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
