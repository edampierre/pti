package com.example.detectionimmobility;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ImmobilityDetector implements SensorEventListener {

    private SensorManager sensorManager;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    private BeepHelper beepHelper;

    private final Context context;

    public ImmobilityDetector(Context context) {
        this.context = context;
    }

    public void startListening() {

        beepHelper = new BeepHelper();

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopListening() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] mGravity = sensorEvent.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;

            float mAccessCurrentSquare = x * x + y * y + z * z;
            mAccelCurrent = (float) Math.sqrt(mAccessCurrentSquare);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            String MYAPP = "DETECTION_IMMOBILITY";
            if (mAccel > 1) {
                Log.d(MYAPP, "Movement Detected");
                beepHelper.beep(100);
            } else {
                Log.d(MYAPP, "Immobility Detected");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
