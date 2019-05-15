package kz.woop.iot.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kz.woop.iot.R;
import kz.woop.iot.utils.Config;
import kz.woop.iot.api.IoTAction;
import kz.woop.iot.api.IoTConnector;
import kz.woop.iot.api.IoTListener;
import kz.woop.iot.utils.MessageFactory;

public class StepSensor implements SensorEventListener {

    private final String TAG = StepSensor.class.getName();
    private static StepSensor instance;
    private final Context context;
    private final Activity activity;
    private final SensorManager sensorManager;
    private final Sensor detector;
    private boolean isRunning = false;


    private int stepCount = 0;
    private ScheduledExecutorService executor;

    private StepSensor(Activity activity) {
        this.activity = activity;
        this.context = activity.getBaseContext();

        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.detector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }


    public static StepSensor getInstance(Activity activity) {
        if (instance == null) {
            Log.i(StepSensor.class.getName(), "Creating new StepSensor");
            instance = new StepSensor(activity);
        }
        return instance;
    }


    public void enableSensor() {
        Log.i(TAG, ".enableSensor() entered");
        if (!isRunning) {
            sensorManager.registerListener(this, detector, SensorManager.SENSOR_DELAY_UI);
            startTimer();
            isRunning = true;
        }
    }


    private void startTimer() {
        Log.i(TAG, ".startTimer() entered");
        this.executor = Executors.newSingleThreadScheduledExecutor();
        long delay = Config.STEP_TIMER_DELAY;
        executor.scheduleAtFixedRate(() -> timerTask(),delay, delay, TimeUnit.SECONDS);
    }


    private void timerTask() {
        Log.i(TAG, ".timerTask() entered");
        IoTConnector connector = IoTConnector.getInstance(context);
        try {
            String event = Config.STEP_EVENT;
            String payload = MessageFactory.stepSensorPayload(stepCount);
            int qos = 0;
            boolean retained = false;
            IoTListener listener = new IoTListener(IoTAction.PUBLISH, context);
            connector.publishEvent(event, payload, qos, retained, listener);
        } catch (MqttException e) {
            Log.d(TAG, ".timerTask() received exception on publishEvent()");
        } finally {
            stepCount = 0;
            activity.runOnUiThread(() -> {
                TextView textView = activity.findViewById(R.id.stepsValue);
                textView.setText(String.valueOf(stepCount));
            });
        }
    }

    public void disableSensor() {
        Log.d(TAG, ".disableSensor() entered");
        if (executor != null && isRunning) {
            try {
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
                sensorManager.unregisterListener(this);
                isRunning = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, ".onSensorChanged() entered");
        if (isRunning) {
            Sensor sensor = event.sensor;
            if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
                stepCount++;
                activity.runOnUiThread(() -> {
                    TextView textView = activity.findViewById(R.id.stepsValue);
                    textView.setText(String.valueOf(stepCount));
                });
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, ".onAccuracyChanged() entered");
    }

}

