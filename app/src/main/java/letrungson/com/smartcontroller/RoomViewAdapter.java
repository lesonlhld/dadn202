package letrungson.com.smartcontroller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomViewAdapter extends RecyclerView.Adapter<RoomViewAdapter.MyViewHolder> {

    private List<Room> roomList;
    private Context context;

    public RoomViewAdapter(Context context,List<Room> roomList) {
        this.roomList = roomList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roomitem_cardview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.roomName.setText(this.roomList.get(position).getRoomName());
        holder.roomDescription.setText(this.roomList.get(position).getRoomDescription());
        holder.roomTemp.setText(this.roomList.get(position).getRoomTemp());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, RoomDetail.class);
//                intent.putExtra("Room",roomList.get(position).toString());
//                context.startActivity(intent);
                Toast.makeText(context, "Clicked" + roomList.get(position).getRoomName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView roomName;
        TextView roomDescription;
        TextView roomTemp;
        public MyViewHolder(View itemView){
            super(itemView);
            roomName = (TextView) itemView.findViewById(R.id.room_item_name);
            roomDescription = (TextView) itemView.findViewById(R.id.room_item_description);
            roomTemp = (TextView) itemView.findViewById(R.id.room_item_temp);
        }
    }

}
