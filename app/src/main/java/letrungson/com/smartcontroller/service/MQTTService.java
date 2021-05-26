package letrungson.com.smartcontroller.service;

import android.content.Context;
import android.util.Log;

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

import letrungson.com.smartcontroller.model.Value;
import letrungson.com.smartcontroller.util.Constant;

public class MQTTService {
    final String serverUri = "tcp://io.adafruit.com:1883";
    final String clientId = MqttClient.generateClientId();
    final String username = "leson0108";
    final String password = "aio_rHhv85FXuO6uVO2wgnOrl0FWF7az";
    final String topic = username + "/feeds/";
    public MqttAndroidClient mqttAndroidClient;

    public MQTTService(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connectAndSubscribe();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connectAndSubscribe() {
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
                    subscribeToTopic();
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

    private void subscribeToTopic() {
        String subscriptionTopic = topic + "#";
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

        } catch (MqttException ex) {
            System.err.println("Exceptionstsubscribing");
            ex.printStackTrace();
        }
    }

    public void sendDataMQTT(String deviceId, String data) {
        String topicOfDevice = topic + deviceId;
        try {
            Value value = Constant.getListFeeds().get(deviceId);
            if (value == null){
                value = new Value("","","","");
            }
            value.setData(data);

            MqttMessage message = new MqttMessage(value.convertToJson().getBytes(Charset.forName("UTF-8")));
            message.setQos(0);
            message.setRetained(true);
            // publish message to broker
            Log.i("mqtt", "Message \"" + deviceId + ": " + data + "\" published");
            mqttAndroidClient.publish(topicOfDevice, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

