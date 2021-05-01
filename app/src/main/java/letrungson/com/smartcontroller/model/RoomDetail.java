package letrungson.com.smartcontroller.model;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import letrungson.com.smartcontroller.R;

public class RoomDetail extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomdetail);
        Toast.makeText(RoomDetail.this, "Clicked", Toast.LENGTH_SHORT).show();
    }
}
