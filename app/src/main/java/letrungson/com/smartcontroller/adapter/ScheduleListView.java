package letrungson.com.smartcontroller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Schedule;

import static java.lang.String.valueOf;

public class ScheduleListView extends BaseAdapter {

    LayoutInflater inflater;
    List<Schedule> schedules;


    public ScheduleListView(Context context, List<Schedule> schedules) {
        this.schedules = schedules;
        inflater = (LayoutInflater.from(context));
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
        view = inflater.inflate(R.layout.schedule_items, null);
        TextView startDay = view.findViewById(R.id.startDay);
        TextView endDay = view.findViewById(R.id.endDay);
        TextView startTime = view.findViewById(R.id.startTime);
        TextView endTime = view.findViewById(R.id.endTime);
        TextView temp = view.findViewById(R.id.temp);
        TextView humid = view.findViewById(R.id.humid);

        startDay.setText(schedules.get(position).getStartDay());
        endDay.setText(schedules.get(position).getEndDay());
        startTime.setText(schedules.get(position).getStartTime());
        endTime.setText(schedules.get(position).getEndTime());
        temp.setText(valueOf(schedules.get(position).getTemp()));
        humid.setText(valueOf(schedules.get(position).getHumid()));

        return view;
    }
}
