package letrungson.com.smartcontroller.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.activity.ScheduleActivity;
import letrungson.com.smartcontroller.activity.ScheduleEditActivity;
import letrungson.com.smartcontroller.model.Schedule;

public class ScheduleListView extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<Schedule> schedules;


    public ScheduleListView(Context context, List<Schedule> schedules) {
        this.schedules = schedules;
        inflater = (LayoutInflater.from(context));
        this.context = context;
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int position) {
        return schedules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.schedule_items,null);
        TextView startDay = view.findViewById(R.id.startDay);
        TextView endDay = view.findViewById(R.id.endDay);
        TextView startTime = view.findViewById(R.id.startTime);
        TextView endTime = view.findViewById(R.id.endTime);
        TextView temp = view.findViewById(R.id.temp);
        TextView humid = view.findViewById(R.id.humid);

        //startDay.setText(schedules.get(position).getStartDay());
        //endDay.setText(schedules.get(position).getEndDay());
        startTime.setText(schedules.get(position).getStartTime());
        endTime.setText(schedules.get(position).getEndTime());

        temp.setText(String.valueOf(schedules.get(position).getTemp()));
        humid.setText(String.valueOf(schedules.get(position).getHumid()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Clicked" + schedules.get(position).getScheduleID(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ScheduleEditActivity.class);
                intent.putExtra("scheduleId",schedules.get(position).getScheduleID());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });
        return view;
    }
}