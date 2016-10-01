package com.brocorporation.cameratest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by leon on 23.09.16.
 */

public class RVOrientationSensor extends OrientationSensor {

    private final Sensor rotSensor;

    final float[] q = new float[5], R = new float[16];

    public RVOrientationSensor(SensorManager sm){
        super(sm);
        rotSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }
    @Override
    public void start() {
        sensorManager.registerListener(this, rotSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            return;
        if (Sensor.TYPE_ROTATION_VECTOR == event.sensor.getType()) {
           // System.arraycopy(event.values,0, q,0, event.values.length);
            SensorManager.getRotationMatrixFromVector(R, event.values);
            if(changeListener!=null)
                changeListener.onOrientationChanged(R,null);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
