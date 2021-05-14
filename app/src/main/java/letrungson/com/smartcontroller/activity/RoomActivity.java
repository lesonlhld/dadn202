package letrungson.com.smartcontroller.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Room;
import letrungson.com.smartcontroller.service.Database;

public class RoomActivity extends Activity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Button addRoom, add;
    TextView roomName, cancel;
    ArrayAdapter<Room> arrayAdapter;
    ListView listView;
    private List<Room> listRoom;
    private DatabaseReference devices, rooms, schedules;
    Database db = new Database();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        devices = database.getReference("devices");
        rooms = database.getReference("rooms");
        schedules = database.getReference("schedules");

        getAllRoom();
        setContentView(R.layout.activity_room);

        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<Room>(this, android.R.layout.simple_list_item_1, listRoom);
        listView.setAdapter(arrayAdapter);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                String roomId = listRoom.get(pos).getroomId();
                Log.d("room", roomId);
                new AlertDialog.Builder(RoomActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Xóa")
                        .setMessage("Bạn muốn xóa phòng này?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeRoom(roomId);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
                return true;
            }
        });

        addRoom = findViewById(R.id.add_room_tbn);
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoom();
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addRoom() {
        setContentView(R.layout.activity_addroom);
        roomName = findViewById(R.id.room_edt_text);

        add = findViewById(R.id.add_tbn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = roomName.getText().toString();
                db.addRoom(name);
                startActivity(new Intent(RoomActivity.this, RoomActivity.class));
                finish();
            }
        });

        cancel = findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoomActivity.this, RoomActivity.class));
                finish();
            }
        });
    }

    public void getAllRoom() {
        listRoom = new ArrayList<>();
        Query allRoom = database.getReference("rooms");
        allRoom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listRoom.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Room room = data.getValue(Room.class);
                    String roomId = data.getKey();
                    room.setroomId(roomId);
                    listRoom.add(room);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void removeRoom(String roomId) {
        rooms.child(roomId).removeValue();
        devices.orderByChild("roomId").equalTo(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    data.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        schedules.orderByChild("roomId").equalTo(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    data.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RoomActivity.this, MainActivity.class));
        finish();
    }
}
