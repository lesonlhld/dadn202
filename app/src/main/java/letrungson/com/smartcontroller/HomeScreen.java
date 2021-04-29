package letrungson.com.smartcontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreeen);
        List<Room> lstRoom = getListData();

        GridView gridView  = findViewById(R.id.gridView);
        RoomGridAdapter roomGridAdapter = new RoomGridAdapter(this, lstRoom);
        gridView.setAdapter(roomGridAdapter);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Object o = gridView.getItemAtPosition(position);
//                Room room = (Room) o;
//                Toast.makeText(HomeScreen.this, "Selected" + " " + room.getRoomName(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }
    private List<Room> getListData(){
        List<Room> lst = new ArrayList<Room>();
        Room bedRoom = new Room("Bed Room",false,"Heat to 30.0","30");
        Room livingRoom = new Room("Living Room",true,"Heat to 25.0","25");
        Room room1= new Room("Room 1",false,"Heat to 30.0","30");
        Room room2= new Room("Room 2",false,"Heat to 30.0","30");
        Room room3= new Room("Room 3",false,"Heat to 30.0","30");
        Room room4= new Room("Room 4",false,"Heat to 30.0","30");
        Room room5= new Room("Room 5",false,"Heat to 30.0","30");
        Room room6= new Room("Room 6",false,"Heat to 30.0","30");
        Room room7= new Room("Room 7",false,"Heat to 30.0","30");
        lst.add(bedRoom);
        lst.add(livingRoom);
        lst.add(room1);
        lst.add(room2);
        lst.add(room3);
        lst.add(room4);
        lst.add(room5);
        lst.add(room6);
        lst.add(room7);
        return lst;
    }
}
