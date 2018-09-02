package helpers;

import android.content.Context;
import android.util.Log;

import com.ntp.openthegate.OTGStatus;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttHelper {
    public MqttAndroidClient mqttAndroidClient;
    public String connectResult;

    public MqttHelper(Context context){
        try{
            mqttAndroidClient = new MqttAndroidClient(context, OTGStatus.mqServerUri + ":" + OTGStatus.mqPort, OTGStatus.mqClientId);
            mqttAndroidClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean b, String s) {
                    Log.w("mqtt", s);
                }

                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    Log.w("Mqtt", mqttMessage.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            Log.w("Mqtt", "Call connect");
            connect();
        } catch (Exception ex){
            Log.w("Mqtt", "Call connect exception");
            ex.printStackTrace();
        }
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(OTGStatus.mqUsername);
        mqttConnectOptions.setPassword(OTGStatus.mqPassword.toCharArray());

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
                public void onFailure(IMqttToken asyncActionToken, Throwable exception){
                    connectResult = "Failed to connect to: " + OTGStatus.mqServerUri + " " + exception.toString();
                    Log.w("Mqtt", connectResult);
                }
            });

        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    public void subscribeToTopic() {
        try {
            Log.w("Mqtt","Topic:" + OTGStatus.mqSubscriptionTopic + " Last:"+ OTGStatus.lastMqSubscriptionTopic);
            if((OTGStatus.mqSubscriptionTopic != OTGStatus.lastMqSubscriptionTopic)&&(!OTGStatus.lastMqSubscriptionTopic.isEmpty()))
            {
                Log.w("Mqtt","Unsubscribing from:" + OTGStatus.lastMqSubscriptionTopic);
                mqttAndroidClient.unsubscribe(OTGStatus.lastMqSubscriptionTopic);
                OTGStatus.lastMqSubscriptionTopic = OTGStatus.mqSubscriptionTopic;
            }

            Log.w("Mqtt","Subscribing to:" + OTGStatus.mqSubscriptionTopic);
            mqttAndroidClient.subscribe(OTGStatus.mqSubscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.w("Mqtt","Subscribed!");
                    publishMessage("{\"cmd\":\"" + OTGStatus.command + "\",\"id\":\"" + OTGStatus.gateUserID +"\",\"pw\":\"" + OTGStatus.gatePassword +"\"}");
                    OTGStatus.mqttStarted = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishMessage(String payload){

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            mqttAndroidClient.publish(OTGStatus.mqPublishTopic, message);
            Log.w("Mqtt", "Message Published");
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
