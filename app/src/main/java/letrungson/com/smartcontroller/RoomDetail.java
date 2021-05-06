package letrungson.com.smartcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RoomDetail extends AppCompatActivity {
    TextView roomName, roomTempSmall, roomTempBig, roomHumid;
    boolean roomState;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomdetail);
        roomName = findViewById(R.id.roomName);
        roomTempSmall = findViewById(R.id.roomdetail_temp_small);
        roomTempBig =findViewById(R.id.roomdetail_temp_big);
        Intent intent = getIntent();
        roomName.setText(intent.getStringExtra("RoomName"));
        roomTempSmall.setText(intent.getStringExtra("RoomTemp"));
        roomTempBig.setText(intent.getStringExtra("RoomTemp"));
        //roomState = intent.getIntExtra("RoomState",0);
        Toast.makeText(RoomDetail.this, "Clicked", Toast.LENGTH_SHORT).show();
    }
}
