package kz.woop.iot;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import kz.woop.iot.api.IoTAction;
import kz.woop.iot.api.IoTCallback;
import kz.woop.iot.api.IoTConnector;
import kz.woop.iot.api.IoTDevice;
import kz.woop.iot.api.IoTListener;
import kz.woop.iot.sensors.StepSensor;


public class MainActivity extends AppCompatActivity {


    /**
     * Organization ID
     * 80415t
     * Device Type
     * Vedro
     * Device ID
     * igoresha-vedro-1
     * Authentication Method
     * use-token-auth
     * Authentication Token
     * igoresha-vedro-1-token-12
     */



    private boolean running = false;
    private SensorManager sensorManager = null;
    private LocationManager locationManager = null;
    private TextView stepsValue = null;
    private TextView locationValue = null;
    private Vibrator mVibrator = null;
    int steps = 0;
    private static final int REQUEST_LOCATION = 123;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        stepsValue = (TextView) findViewById(R.id.stepsValue);
        locationValue = (TextView) findViewById(R.id.locationValue);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);



        /**
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
        */

        String organizationId = "80415t";
        String deviceType = "Vedro";
        String deviceId = "igoresha-vedro-1";
        String authenticationMethod = "use-token-auth";
        String token = "igoresha-vedro-1-token-12";


        final IoTDevice device = new IoTDevice(organizationId, deviceType, deviceId, authenticationMethod, token);

        final Context context = getApplicationContext();

        final MemoryPersistence persistence = new MemoryPersistence();

        final IoTConnector connector = IoTConnector.getInstance(context, device, persistence);

        final IoTCallback callback = IoTCallback.getInstance();

        StepSensor stepSensor = StepSensor.getInstance(this);


        Button connButton =(Button) findViewById(R.id.conBut);
        Button discButton = (Button) findViewById(R.id.discBut);
        Button pubButton = (Button) findViewById(R.id.pubBut);

                connButton.setText("connect");
        connButton.setOnClickListener(v -> {
            IoTListener listener = new IoTListener(IoTAction.CONNECTING, context);
            connector.connect(listener, callback);
            stepSensor.enableSensor();
        });

        discButton.setText("disconnect");
        discButton.setOnClickListener(v -> {


            IoTListener listener = new IoTListener(IoTAction.DISCONNECTING, context);
            connector.disconnect(listener);
            stepSensor.disableSensor();

        });

        pubButton.setText("turn on sensor");
        //pubButton.setOnClickListener(v -> stepSensor.enableSensor());

    }

    @Override
    protected void onResume() {
        super.onResume();


        /**
        running = true;
        Sensor stepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor stepsDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


        if (stepsDetector == null) {
            Toast.makeText(this, "No Sensor", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.registerListener(stepListener, stepsDetector, SensorManager.SENSOR_DELAY_UI);
        }

        GPSSensor gpsListener = new GPSSensor();



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No GPS permissions", Toast.LENGTH_SHORT).show();

        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, gpsListener);

        Toast.makeText(this, "Работает", Toast.LENGTH_SHORT).show();
        **/
    }

    @Override
    protected void onPause() {
        super.onPause();

    }



}


