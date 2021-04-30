package letrungson.com.smartcontroller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String TAG = "Database";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase;
    public Database(String path){
        mDatabase = database.getReference(path);
    }

    public void addSensorLog(Data o, String roomid) {
        String id = "Sensor" + mDatabase.push().getKey();
        mDatabase.child(id).setValue(o);
        mDatabase.child(id).child("roomid").setValue(roomid);
    }

    public boolean checkValidUser(String username, String password){

        return false;
    }

    public void addUser(String username, String password) {
        String id = "User" + mDatabase.push().getKey();
        User user = new User(username, password);
        mDatabase.child(id).setValue(user);
    }

/*
    public List<User> getAllUser(){
        List<User> allUsers = new ArrayList<User>();
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


    public List<Room> getAllRoom(){
        List<Room> allRooms = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allRooms.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String roomName = data.getValue(String.class);
                    String roomId = data.getKey();
                    Room room = new Room(roomId, roomName);
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
