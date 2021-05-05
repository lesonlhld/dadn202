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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.RoomViewAdapter;
import letrungson.com.smartcontroller.SpacingItemDecorator;
import letrungson.com.smartcontroller.model.Room;
import letrungson.com.smartcontroller.service.Database;

public class RoomActivity extends Activity {
    ImageButton moreButton, homeButton;
    Button room_btn, addRoom, reload, add;
    TextView roomName, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Database db = new Database();
        List<Room> lstRoom;
        lstRoom = db.getAllRoom();

        setContentView(R.layout.activity_room);

        homeButton = findViewById(R.id.home_btn);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoomActivity.this, MainActivity.class));
            }
        });

        moreButton = findViewById(R.id.list_btn);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoomActivity.this, MoreActivity.class));
            }
        });

        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<Room> arrayAdapter = new ArrayAdapter<Room>(this, android.R.layout.simple_list_item_1 , lstRoom);
        listView.setAdapter(arrayAdapter);

        addRoom = findViewById(R.id.add_room_tbn);
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoom(db);
                arrayAdapter.notifyDataSetChanged();
            }
        });

        reload = findViewById(R.id.reload_room_tbn);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void addRoom(Database db){
        setContentView(R.layout.activity_addroom);
        roomName = findViewById(R.id.room_edt_text);

        add = findViewById(R.id.add_tbn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = roomName.getText().toString();
                db.addRoom(name);
//                setContentView(R.layout.activity_room);

                startActivity(new Intent(RoomActivity.this, RoomActivity.class));
            }
        });

        cancel = findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setContentView(R.layout.activity_room);
                startActivity(new Intent(RoomActivity.this, RoomActivity.class));
            }
        });
    }
}
