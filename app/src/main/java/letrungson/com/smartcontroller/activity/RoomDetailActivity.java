package letrungson.com.smartcontroller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import static java.lang.String.valueOf;

public class RoomDetailActivity extends Activity {
    private final DatabaseReference rooms = FirebaseDatabase.getInstance().getReference();
    private Room thisRoom;
    TextView roomName, temperature, humidity, targetTemp;
    ConstraintLayout smart_schedule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final String roomId =intent.getStringExtra("roomId");
        getRoom(roomId);

        setContentView(R.layout.roomdetail);
        roomName = findViewById(R.id.roomName);
        temperature = findViewById(R.id.roomdetail_temp_small);
        humidity = findViewById(R.id.roomdetail_humid);
        targetTemp = findViewById(R.id.roomdetail_temp_big);

        smart_schedule = findViewById(R.id.smart_schedule);
        smart_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoomDetailActivity.this, ScheduleActivity.class));
            }
        });
    }

    public void getRoom(String roomId){
        Query roomDb = rooms.child("rooms").child(roomId);
        roomDb.keepSynced(true);
        roomDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    thisRoom = dataSnapshot.getValue(Room.class);
                    Log.d("roomName", thisRoom.getRoomName());
                    roomName.setText(thisRoom.getRoomName());
                    temperature.setText(thisRoom.getRoomCurrentTemp());
                    humidity.setText(thisRoom.getRoomCurrentHumidity());
                    targetTemp.setText(thisRoom.getRoomTargetTemp());
                }
                else
                {
                    Log.d("room", "Database is empty now!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
