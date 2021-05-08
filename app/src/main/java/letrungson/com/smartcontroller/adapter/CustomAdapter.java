package letrungson.com.smartcontroller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.model.Device;

public class CustomAdapter extends ArrayAdapter<Device> {
    private int layout;
    private FirebaseDatabase db;
    private DatabaseReference dbRefDevices;


    public CustomAdapter(Context context, int resource, List<Device> objects) {
        super(context, resource, objects);
        layout = resource;
        db = FirebaseDatabase.getInstance();
        dbRefDevices = db.getReference("devices");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mainViewholder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            Device device = getItem(position);
            viewHolder.title = (TextView) convertView.findViewById(R.id.list_ac_item_text);
            viewHolder.title.setText(device.getDeviceName());
            viewHolder.switchCompat = (SwitchCompat) convertView.findViewById(R.id.list_ac_item_btn);
            if (device.getState() != null) {
                viewHolder.switchCompat.setChecked(device.getState().equals("On"));
            }

            viewHolder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        dbRefDevices.child(device.getDeviceId()).child("state").setValue("On");
                    } else {
                        dbRefDevices.child(device.getDeviceId()).child("state").setValue("Off");
                    }
                }

            });
            convertView.setTag(viewHolder);
        } else {
            mainViewholder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public class ViewHolder {
        TextView title;
        SwitchCompat switchCompat;
    }
}