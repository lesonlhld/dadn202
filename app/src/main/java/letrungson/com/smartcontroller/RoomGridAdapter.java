package letrungson.com.smartcontroller;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomGridAdapter extends BaseAdapter {
    private final List<Room> listRoom;
    private final Context context;

    public RoomGridAdapter(Context context, List<Room> listRoom) {
        this.listRoom = listRoom;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listRoom.size();
    }

    @Override
    public Object getItem(int position) {
        return listRoom.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.roomitem_cardview, null);
        }

        TextView roomName = (TextView) convertView.findViewById(R.id.room_item_name);
        TextView roomDescription = (TextView) convertView.findViewById(R.id.room_item_description);
        TextView roomTemp = (TextView) convertView.findViewById(R.id.room_item_temp);
        //ImageButton roomPowerBtn = (ImageButton) convertView.findViewById(R.id.room_item_powerbtn);
        roomName.setText(listRoom.get(position).getRoomName());
        roomDescription.setText(listRoom.get(position).getRoomDescription());
        roomTemp.setText(listRoom.get(position).getRoomTemp());

//        if(listRoom.get(position).getRoomState()) roomPowerBtn.setColorFilter(0);
//        else roomPowerBtn.setColorFilter(15861768);
        return convertView;
    }
}
