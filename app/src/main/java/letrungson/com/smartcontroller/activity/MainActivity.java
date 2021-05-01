package letrungson.com.smartcontroller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.benlypan.usbhid.OnUsbHidDeviceListener;
//import com.benlypan.usbhid.UsbHidDevice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import letrungson.com.smartcontroller.BuildConfig;
import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.RoomViewAdapter;
import letrungson.com.smartcontroller.SpacingItemDecorator;
import letrungson.com.smartcontroller.model.Data;
import letrungson.com.smartcontroller.model.Device;
import letrungson.com.smartcontroller.model.Room;
import letrungson.com.smartcontroller.service.Database;
import letrungson.com.smartcontroller.service.MQTTService;

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
    private FirebaseAuth mAuth;
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

        mAuth = FirebaseAuth.getInstance();
        checkCurrentUser(mAuth);

        Database sensor = new Database("sensors");
        Database logs = new Database("logs");
        Database devices = new Database("devices");
        Database room = new Database("rooms");
        List<Room> lstRoom = room.getAllRoom();

        setContentView(R.layout.homescreeen);

        RecyclerView recyclerView = findViewById(R.id.gridView);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        SpacingItemDecorator itemDecorator = new SpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        RoomViewAdapter roomViewAdapter = new RoomViewAdapter(MainActivity.this,lstRoom);
        recyclerView.setAdapter(roomViewAdapter);
        Button reload = findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                recyclerView.setAdapter(new RoomViewAdapter(MainActivity.this,lstRoom));
            }
        });
//        recyclerView.setAdapter(new RoomViewAdapter(MainActivity.this,lstRoom));
//        recyclerView.invalidate();
//        GridView gridView  = findViewById(R.id.gridView);
//        RoomGridAdapter roomGridAdapter = new RoomGridAdapter(this, lstRoom);
//        gridView.setAdapter(roomGridAdapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getApplicationContext(),RoomDetail.class);
//                intent.putExtra("roomName", gridView.getItemAtPosition(position).toString());
//                startActivity(intent);
//            }
//        });
//        temperature = findViewById(R.id.temperature);
//        humidity = findViewById(R.id.humidity);

        mqttService = new MQTTService( this);
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

                if (topic.indexOf("/json") != -1) {
                    String context = topic.substring(topic.lastIndexOf('/', topic.lastIndexOf('/')-1) + 1, topic.lastIndexOf('/'));
                    Data dataMqtt = null;
                    dataMqtt = new Gson().fromJson(data_to_microbit, new TypeToken<Data>() {
                    }.getType());
                    if(!context.equals(dataMqtt.getId())){
                        String roomid = "test";
                        int inGroup = topic.indexOf('.');
                        if (inGroup != -1) {
                            roomid = topic.substring(topic.lastIndexOf('/', inGroup) + 1, inGroup);
                            if(topic.indexOf("temperature") != -1 ){
                                checkExistRoom(roomid, lstRoom, room, dataMqtt.getLast_value());
                            }else{
                                checkExistRoom(roomid, lstRoom, room, null);
                            }
                            Log.w("Room", roomid);
                        }
                        if (topic.indexOf("temperature") != -1 || topic.indexOf("humidity") != -1) {
                            Log.d(topic, data_to_microbit);
                            if(topic.indexOf("temperature") != -1 ){
                                int index = checkExistRoom(roomid, lstRoom, room, dataMqtt.getLast_value());
                                Room r = lstRoom.get(index);
                                r.setRoomTemp(dataMqtt.getLast_value());
                                lstRoom.set(index, r);
                                room.updateRoom(roomid, dataMqtt.getLast_value());
                            }
                            sensor.addSensorLog(dataMqtt, roomid);
                            //                    humidity.setText(dataObject.getLast_value());
                        } else {//Devices
                            Log.d(topic, data_to_microbit);
                            logs.addLog(dataMqtt.getId(), dataMqtt.getLast_value());
                            devices.updateDevice(dataMqtt.getId(), dataMqtt.getKey(), dataMqtt.getLast_value());
                        }
                    }
                }
            }

            @Override
            public void deliveryComplete( IMqttDeliveryToken token) {

            }
        });

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

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


    private void checkCurrentUser(FirebaseAuth mAuth) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            port.close();
        }catch (Exception e) {

        }
    }

    private int checkExistRoom(String roomid, List<Room> lstRoom, Database rooms, String temp){
        int index = 0;
        for (Room room: lstRoom){
            if(roomid.equals(room.getRoomId())){
                return index;
            }
            index++;
        }
        rooms.addRoom(roomid, "Test");
        rooms.updateRoom(roomid, temp);
        return -1;
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
}

