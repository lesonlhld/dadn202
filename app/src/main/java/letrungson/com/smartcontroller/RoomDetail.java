package letrungson.com.smartcontroller;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RoomDetail extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomdetail);
        Toast.makeText(RoomDetail.this, "Clicked", Toast.LENGTH_SHORT).show();
    }
}
