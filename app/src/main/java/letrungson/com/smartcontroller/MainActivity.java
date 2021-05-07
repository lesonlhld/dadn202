package letrungson.com.smartcontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.benlypan.usbhid.OnUsbHidDeviceListener;
//import com.benlypan.usbhid.UsbHidDevice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

//import es.rcti.printerplus.printcom.models.PrintTool;
//import es.rcti.printerplus.printcom.models.StructReport;

//https://github.com/rcties/PrinterPlusCOMM
//https://github.com/mik3y/usb-serial-for-android
public class MainActivity extends AppCompatActivity  implements SerialInputOutputManager.Listener{
    private static final String ACTION_USB_PERMISSION = "com.android.recipes.USB_PERMISSION";
    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

    TextView temperature;
    TextView humidity;
    UsbSerialPort port;

    MQTTService mqttService;

    private Thread readThread;

    private boolean mRunning;


    //private UsbHidDevice device = null;

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        setContentView(R.layout.homescreeen);
        List<Room> lstRoom = getListData();
        RecyclerView recyclerView = findViewById(R.id.gridView);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        RoomViewAdapter roomViewAdapter = new RoomViewAdapter(MainActivity.this,lstRoom);
        recyclerView.setAdapter(roomViewAdapter);
*/
        setContentView(R.layout.setscheduled);
        ListView listView = findViewById(R.id.smart_schedule_listview);
        List<Schedule> lst = getListSchedule();
        ScheduleListView scheduleListView = new ScheduleListView(getApplicationContext(),lst);
        listView.setAdapter(scheduleListView);
//        temperature = findViewById(R.id.temperature);
//        humidity = findViewById(R.id.humidity);

/*        mqttService = new MQTTService( this);
        //mqttService = new MQTTService( getApplicationContext());
        mqttService.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost( Throwable throwable){

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String data_to_microbit = message.toString();
                //port.write(data_to_microbit.getBytes(),1000);

//                if(topic.indexOf("temperature/json") != -1){
//                    Data dataObject = new Gson().fromJson(data_to_microbit, new TypeToken<Data>() {}.getType());
//                    Log.d(topic, data_to_microbit);
//                    temperature.setText(dataObject.getLast_value());
//                    temperature.append("*C");
//                }
//                if(topic.indexOf("humidity/json") != -1){
//                    Data dataObject = new Gson().fromJson(data_to_microbit, new TypeToken<Data>() {}.getType());
//                    Log.d(topic, data_to_microbit);
//                    humidity.setText(dataObject.getLast_value());
//                    humidity.append("%");
//                }


            }

            @Override
            public void deliveryComplete( IMqttDeliveryToken token) {

            }
        });

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);*/

/*
        if (availableDrivers.isEmpty()) {
            Log.d("UART", "UART is not available");
            txtOut.setText("UART is not available");
        }else {
            Log.d("UART", "UART is available");
            txtOut.setText("UART is available");

            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                manager.requestPermission(driver.getDevice(), usbPermissionIntent);
                manager.requestPermission(driver.getDevice(), PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));
            } else {
                // Most devices have just one port (port 0)
                port = driver.getPorts().get(0);
                try {
                    port.open(connection);
                    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                    port.write("hello abc#".getBytes(), 1000);
                    txtOut.setText("A sample string is sent");

                    SerialInputOutputManager usbIoManager = new SerialInputOutputManager(port, this);
                    Executors.newSingleThreadExecutor().submit(usbIoManager);
                } catch (Exception e) {
                    txtOut.setText("Sending a message is fail");
                }

            }
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            port.close();
        }catch (Exception e) {

        }
    }


    @Override
    public void onNewData(final byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temperature.append(Arrays.toString(data));
                humidity.append(Arrays.toString(data));
            }
        });
    }

    @Override
    public void onRunError(Exception e) {

    }

    private void sendDataMQTT( String data){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

        byte[] b = data.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        Log.d("ABC","Publish: " + msg);
        try {
            mqttService.mqttAndroidClient.publish("[Your path to the feed you want to send message]", msg);
        } catch ( MqttException e){
            Log.d("MQTT", "sendDataMQTT: cannot send message");
        }
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
    private List<Schedule> getListSchedule(){
        List<Schedule> lst = new ArrayList<Schedule>();
        Schedule schedule = new Schedule("Fri","Sat","25.0C","70%","7:00","12:00");
        Schedule schedule1 = new Schedule("Mon","Sat","15.0C","50%","13:00","12:00");
        Schedule schedule2 = new Schedule("Tue","Sat","22.0C","90%","10:00","12:00");
        lst.add(schedule);
        lst.add(schedule1);
        lst.add(schedule2);
        return lst;
    }
}

