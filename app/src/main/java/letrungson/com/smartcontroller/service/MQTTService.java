package letrungson.com.smartcontroller.service;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import letrungson.com.smartcontroller.activity.MainActivity;
import letrungson.com.smartcontroller.activity.SplashActivity;
import letrungson.com.smartcontroller.model.Data;
import letrungson.com.smartcontroller.model.Value;
import letrungson.com.smartcontroller.tools.Check;
import letrungson.com.smartcontroller.util.Constant;

import static letrungson.com.smartcontroller.tools.Check.checkExistDevice;

public class MQTTService {
    final String serverUri = "tcp://io.adafruit.com:1883";
    final HashMap<String, String> clientId;
    public HashMap<String, MqttAndroidClient> listMqttAndroidClient = new HashMap<String, MqttAndroidClient>();

    public MQTTService(Context context){
        clientId = new HashMap<String, String>();
        for (String name: Constant.getServerInfo().keySet()) {
            String username = name.toString();
            String password = Constant.getServerInfo().get(username).toString();
            clientId.put(username, MqttClient.generateClientId());
            MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId.get(username));
            mqttAndroidClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean b, String s) {
                }

                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if (topic.indexOf("/json") != -1) {
                        String data_to_microbit = message.toString();
                        String context = topic.substring(topic.lastIndexOf('/', topic.lastIndexOf('/') - 1) + 1, topic.lastIndexOf('/'));
                        Data dataMqtt = new Gson().fromJson(data_to_microbit, new TypeToken<Data>() {
                        }.getType());
                        if (checkExistDevice(MainActivity.allDevices, dataMqtt.getKey()) && context.equals(dataMqtt.getKey())) {
                            Log.d("MQTT write to database", data_to_microbit);
                            Value valueMqtt = new Gson().fromJson(dataMqtt.getLast_value(), new TypeToken<Value>() {
                            }.getType());
                            Database.updateData(dataMqtt.getKey(), dataMqtt, valueMqtt);
                            Database.updateDevice(dataMqtt.getKey(), valueMqtt.getData());
                            //port.write(data_to_microbit.getBytes(),1000);
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            connectAndSubscribe(mqttAndroidClient, username, password);
        }
    }

    public void setCallback(MqttCallbackExtended callback) {
        for (String name: listMqttAndroidClient.keySet()) {
            listMqttAndroidClient.get(name).setCallback(callback);
        }
    }

    private void connectAndSubscribe(MqttAndroidClient mqttAndroidClient, String username, String password) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic(mqttAndroidClient, username);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to:" + serverUri + exception.toString());
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private void subscribeToTopic(MqttAndroidClient mqttAndroidClient, String server) {
        String subscriptionTopic = server + "/feeds/" + "#";
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt", "Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribedfail!");
                }
            });
            listMqttAndroidClient.put(server, mqttAndroidClient);
        } catch (MqttException ex) {
            System.err.println("Exceptionstsubscribing");
            ex.printStackTrace();
        }
    }

    public void sendDataMQTT(String server, String deviceId, String data) {
        String topicOfDevice = server + "/feeds/" + deviceId;
        try {
            Value value = Check.checkAndGetValueOfDevice(deviceId);
            value.setData(data);

            MqttMessage message = new MqttMessage(value.convertToJson().getBytes(Charset.forName("UTF-8")));
            message.setQos(0);
            message.setRetained(true);
            // publish message to broker
            Log.i("mqtt", "Message \"" + deviceId + ": " + data + "\" published");
            listMqttAndroidClient.get(server).publish(topicOfDevice, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

