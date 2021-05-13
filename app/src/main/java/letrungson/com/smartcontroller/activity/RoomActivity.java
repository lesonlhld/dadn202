package letrungson.com.smartcontroller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Room;
import letrungson.com.smartcontroller.model.RoomDetail;
import letrungson.com.smartcontroller.service.Database;

public class RoomActivity extends Activity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    ImageButton moreButton, homeButton;
    Button room_btn, addRoom, add;
    TextView roomName, cancel;
    ArrayAdapter<Room> arrayAdapter;
    ListView listView;
    private List<Room> listRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Database db = new Database();
        getAllRoom();
        setContentView(R.layout.activity_room);

        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<Room>(this, android.R.layout.simple_list_item_1, listRoom);
        listView.setAdapter(arrayAdapter);

        addRoom = findViewById(R.id.add_room_tbn);
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoom(db);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        room_btn = findViewById(R.id.room_manage_btn);
        room_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void addRoom(Database db) {
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
                    room.setRoomId(roomId);
                    listRoom.add(room);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RoomActivity.this, MainActivity.class));
        finish();
    }
}
