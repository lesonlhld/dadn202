package letrungson.com.smartcontroller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.activity.RoomDetailActivity;
import letrungson.com.smartcontroller.model.Room;

public class RoomViewAdapter extends RecyclerView.Adapter<RoomViewAdapter.MyViewHolder> {
    private List<Room> roomList;
    private Context context;

    public RoomViewAdapter(Context context, List<Room> roomList) {
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
        if (this.roomList.get(position).getRoomTargetTemp() != null) {
            holder.roomTargetTemp.setText("Heat to " + this.roomList.get(position).getRoomTargetTemp());
        } else {
            holder.roomTargetTemp.setText("No schedule");
        }
        holder.roomCurrentTemp.setText(this.roomList.get(position).getRoomCurrentTemp());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Clicked: " + roomList.get(position).getRoomName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, RoomDetailActivity.class);
                intent.putExtra("roomId", roomList.get(position).getroomId());
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView roomName;
        TextView roomTargetTemp;
        TextView roomCurrentTemp;

        public MyViewHolder(View itemView) {
            super(itemView);
            roomName = (TextView) itemView.findViewById(R.id.room_item_name);
            roomTargetTemp = (TextView) itemView.findViewById(R.id.room_item_description);
            roomCurrentTemp = (TextView) itemView.findViewById(R.id.room_item_temp);
        }
    }
}
