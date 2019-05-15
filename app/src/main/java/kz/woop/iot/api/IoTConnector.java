package kz.woop.iot.api;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;

import kz.woop.iot.utils.Config;

public class IoTConnector {

    private static final String TAG = IoTConnector.class.getName();
    private static IoTConnector instance;

    private final Context context;

    private MqttAndroidClient mqttClient;
    private MemoryPersistence persistence;
    private IoTDevice device;


    private IoTConnector(Context context, IoTDevice device, MemoryPersistence persistence) {
        this.context = context;
        this.persistence = persistence;
        this.device = device;
    }

    private IoTConnector(Context context) {
        this.context = context;
    }


    public static IoTConnector getInstance(Context context, IoTDevice device, MemoryPersistence persistence) {
        Log.d(TAG, ".getInstance() entered");
        if (instance == null) {
            instance = new IoTConnector(context, device, persistence);
        }
        return instance;
    }

    public static IoTConnector getInstance(Context context) {
        Log.d(TAG, ".getInstance() entered");
        if (instance == null) {
            instance = new IoTConnector(context);
        }
        return instance;
    }


    private boolean isClientConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }


    /**
     * @param listener
     * @param callback
     *
     * @return
     */
    public IMqttToken connect(IoTListener listener, IoTCallback callback) {

        String clientId = "d:" + device.getOrganizationId() + ":" + device.getDeviceType() + ":" + device.getDeviceId();
        final String connectionURI;
        //connectionURI = "tcp://" + device.getOrganizationId() + Config.IOT_ORGANIZATION_TCP;
        connectionURI = "ssl://" + device.getOrganizationId() + Config.IOT_ORGANIZATION_SSL;

        if (!isClientConnected()) {
            if (mqttClient != null) {
                mqttClient.unregisterResources();
                mqttClient = null;
            }

            mqttClient = new MqttAndroidClient(context, connectionURI, clientId, persistence);
            mqttClient.setCallback(callback);

            try {
                MqttConnectOptions mqttConnectOptions = prepareOptions(device);
                return mqttClient.connect(mqttConnectOptions, context, listener);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @param device
     *
     * @return
     */
    private MqttConnectOptions prepareOptions(IoTDevice device) {
        String username = device.getAuthenticationMethod();
        String password = device.getToken();

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        return mqttConnectOptions;

    }


    /**
     * @param listener
     *
     * @return
     */
    public IMqttToken disconnect(IoTListener listener) {
        Log.d(TAG, ".disconnectDevice() entered");
        if (isClientConnected()) {
            try {
                return mqttClient.disconnect(context, listener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to disconnect from server", e.getCause());
                //throw e;
            }
        }
        return null;
    }


    /**
     * @param event     The event to create a topic string for
     *
     * @return The event topic for the specified event string
     */
    public static String getEventTopic(String event) {
        return "iot-2/evt/" + event + "/fmt/json";
    }


    /**
     * @param command   The command to create a topic string for
     *
     * @return The command topic for the specified command string
     */
    public static String getCommandTopic(String command) {
        return "iot-2/cmd/" + command + "/fmt/json";
    }


    /**
     * Subscribe to an MQTT topic
     *
     * @param topic         The MQTT topic string to subscribe to
     * @param qos           The Quality of Service to use for the subscription
     * @param userContext   The context to associate with the subscribe call
     * @param listener      The IoTListener object to register with the Mqtt Token
     *
     * @@return IMqttToken The token returned by the Mqtt Subscribe call
     *
     * @throws MqttException
     */
    private IMqttToken subscribe(String topic, int qos, Object userContext, IoTListener listener) throws MqttException {
        Log.d(TAG, ".subscribe() entered");
        if (isClientConnected()) {
            try {
                return mqttClient.subscribe(topic, qos, userContext, listener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to subscribe to topic " + topic, e.getCause());
                throw e;
            }
        }
        return null;
    }


    /**
     * Unsubscribe from an MQTT topic
     *
     * @param topic         The MQTT topic string to unsubscribe from
     * @param userContext   The context to associate with the unsubscribe call
     * @param listener      The IoTListener object to register with the Mqtt Token
     *
     * @@return IMqttToken The token returned by the Mqtt Unsubscribe call
     *
     * @throws MqttException
     */
    private IMqttToken unsubscribe(String topic, Object userContext, IoTListener listener) throws MqttException {
        Log.d(TAG, ".unsubscribe() entered");
        if (isClientConnected()) {
            try {
                return mqttClient.unsubscribe(topic, userContext, listener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to subscribe to topic " + topic, e.getCause());
                throw e;
            }
        }
        return null;
    }


    /**
     * Subscribe to a device event
     *
     * @param event         The IoT event to subscribe to
     * @param qos           The Quality of Service to use for the subscription
     * @param userContext   The context to associate with the subscribe call
     * @param listener      The IoTListener object to register with the Mqtt Token
     *
     * @@return IMqttToken The token returned by the Mqtt Subscribe call
     *
     * @throws MqttException
     */
    public IMqttToken subscribeToEvent(String event, int qos, Object userContext, IoTListener listener) throws MqttException {
        Log.d(TAG, ".subscribeToEvent() entered");
        String eventTopic = getEventTopic(event);
        return subscribe(eventTopic, qos, userContext, listener);
    }


    /**
     * Unsubscribe from a device event
     *
     * @param event         The IoT event to unsubscribe from
     * @param userContext   The context to associate with the unsubscribe call
     * @param listener      The IoTListener object to register with the Mqtt Token
     *
     * @@return IMqttToken The token returned by the Mqtt Unsubscribe call
     *
     * @throws MqttException
     */
    public IMqttToken unsubscribeFromEvent(String event, Object userContext, IoTListener listener) throws MqttException {
        Log.d(TAG, ".unsubscribeFromEvent() entered");
        String eventTopic = getEventTopic(event);
        return unsubscribe(eventTopic, userContext, listener);
    }

    /**
     * Subscribe to a device  command
     *
     * @param command       The IoT command to subscribe to
     * @param qos           The Quality of Service to use for the subscription
     * @param userContext   The context to associate with the subscribe call
     * @param listener      The IoTListener object to register with the Mqtt Token
     *
     * @@return IMqttToken The token returned by the Mqtt Subscribe call
     *
     * @throws MqttException
     */
    public IMqttToken subscribeToCommand(String command, int qos, Object userContext, IoTListener listener) throws MqttException {
        Log.d(TAG, "subscribeToCommand() entered");
        String commandTopic = getCommandTopic(command);
        return subscribe(commandTopic, qos, userContext, listener);
    }

    /**
     * Unsubscribe from a device command
     *
     * @param command       The IoT command to unsubscribe from
     * @param userContext   The context to associate with the unsubscribe call
     * @param listener      The IoTListener object to register with the Mqtt Token
     *
     * @@return IMqttToken The token returned by the Mqtt Unsubscribe call
     *
     * @throws MqttException
     */
    public IMqttToken unsubscribeFromCommand(String command, Object userContext, IoTListener listener) throws MqttException {
        Log.d(TAG, ".unsubscribeFromCommand() entered");
        String commandTopic = getCommandTopic(command);
        return unsubscribe(commandTopic, userContext, listener);
    }

    /**
     * Publish to an MQTT topic
     *
     * @param topic     The MQTT topic string to publish the message to
     * @param payload   The payload to be sent
     * @param qos       The Quality of Service to use when publishing the message
     * @param retained  The flag to specify whether the message should be retained
     * @param listener  The IoTListener object to register with the Mqtt Token
     *
     * @return IMqttDeliveryToken The token returned by the Mqtt Publish call
     *
     * @throws MqttException
     */
    private IMqttDeliveryToken publish(String topic, String payload, int qos, boolean retained, IoTListener listener) {

        if (isClientConnected()) {
            try {
                byte[] encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                message.setRetained(retained);
                message.setQos(qos);
                Log.d(TAG, ".publish() - Publishing " + payload + " to: " + topic + ", with QoS: " + qos + " with retained flag set to " + retained);
                return mqttClient.publish(topic, message, context, listener);
            } catch (MqttPersistenceException e) {
                Log.e(TAG, "MqttPersistenceException caught while attempting to publish a message", e.getCause());
                //throw e;
            } catch (MqttException e) {
                Log.e(TAG, "MqttException caught while attempting to publish a message", e.getCause());
                //throw e;
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "UnsupportedEncodingException caught while attempting to publish a message", e.getCause());
            }
        }
        return null;
    }


    /**
     * Publish a device event message
     *
     * @param event     The IoT event string to publish the message to
     * @param payload   The payload to be sent
     * @param qos       The Quality of Service to use when publishing the message
     * @param retained  The flag to specify whether the message should be retained
     * @param listener  The IoTActionListener object to register with the Mqtt Token
     *
     * @@return IMqttDeliveryToken The token returned by the Mqtt Publish call
     *
     * @throws MqttException
     */
    public IMqttDeliveryToken publishEvent(String event, String payload, int qos, boolean retained, IoTListener listener) throws MqttException {
        Log.d(TAG, ".publishEvent() entered");
        String eventTopic = getEventTopic(event);
        return publish(eventTopic, payload, qos, retained, listener);
    }


    /**
     * Publish a device command message
     *
     * @param command   The IoT command to publish the message to
     * @param payload   The payload to be sent
     * @param qos       The Quality of Service to use when publishing the message
     * @param retained  The flag to specify whether the message should be retained
     * @param listener  The IoTActionListener object to register with the Mqtt Token
     *
     * @@return IMqttDeliveryToken The token returned by the Mqtt Publish call
     *
     * @throws MqttException
     */
    public IMqttDeliveryToken publishCommand(String command, String payload, int qos, boolean retained, IoTListener listener) throws MqttException {
        Log.d(TAG, ".publishCommand() entered");
        String commandTopic = getCommandTopic(command);
        return publish(commandTopic, payload, qos, retained, listener);
    }

}
