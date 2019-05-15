package kz.woop.iot.api;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class IoTCallback implements MqttCallback {
    private final static String TAG = IoTCallback.class.getName();
    private static IoTCallback myIoTCallback;

    private IoTCallback() {

    }

    public static IoTCallback getInstance() {
        if (myIoTCallback == null) {
            myIoTCallback = new IoTCallback();
        }
        return myIoTCallback;
    }

    /**
     * Handle loss of connection from the MQTT server.
     * @param throwable The cause of the connection loss
     */
    @Override
    public void connectionLost(Throwable throwable) {
        Log.e(TAG, ".connectionLost() entered");

        if (throwable != null) {
            throwable.printStackTrace();
        }

    }

    /**
     * Process incoming messages to the MQTT client.
     *
     * @param topic       The topic the message was received on.
     * @param mqttMessage The message that was received
     * @throws Exception  Exception that is thrown if the message is to be rejected.
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        Log.d(TAG, ".messageArrived() entered");


        String payload = new String(mqttMessage.getPayload());
        Log.d(TAG, ".messageArrived - Message received on topic " + topic
                + ": message is " + payload);

    }

    /**
     * Handle notification that message delivery completed successfully.
     *
     * @param iMqttDeliveryToken The token corresponding to the message which was delivered.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.d(TAG, ".deliveryComplete() entered");
    }
}
