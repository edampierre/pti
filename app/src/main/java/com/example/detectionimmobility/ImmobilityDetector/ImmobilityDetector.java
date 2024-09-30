package com.example.detectionimmobility.ImmobilityDetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ImmobilityDetector implements SensorEventListener {

    private SensorManager sensorManager;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    private final Context context;

    private ImmobilityDetectedCallback immobilityDetectedCallback = null;
    private MotionDetectedCallback motionDetectedCallback = null;

    public ImmobilityDetector(Context context) {
        this.context = context;
    }

    public void startListening() {

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
            if (mAccel > 1) {
                if(this.motionDetectedCallback != null) {
                    this.motionDetectedCallback.onMotionDetected();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void registerImmobilityCallback(ImmobilityDetectedCallback immobilityDetectorCallback) {
        this.immobilityDetectedCallback = immobilityDetectorCallback;
    }

    public void registerMotionCallback(MotionDetectedCallback motionDetectedCallback) {
        this.motionDetectedCallback = motionDetectedCallback;
    }
}
