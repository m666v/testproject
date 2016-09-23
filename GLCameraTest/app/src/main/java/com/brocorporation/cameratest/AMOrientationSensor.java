package com.brocorporation.cameratest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by leon on 23.09.16.
 */

public class AMOrientationSensor extends OrientationSensor {

    private final Sensor accSensor, magSensor;

    final float[] gravity = new float[3], geomag = new float[3], R = new float[16];
    byte calcRotationMatrix = 0;

    public AMOrientationSensor(SensorManager sm){
        super(sm);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }
    @Override
    public void start() {
        calcRotationMatrix = 0;
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            return;
        if (Sensor.TYPE_ACCELEROMETER == event.sensor.getType()) {
            if (calcRotationMatrix != 3) {
                calcRotationMatrix += 1;
                System.arraycopy(event.values, 0, gravity, 0, event.values.length);
            } else Utils.lowPass(gravity, event.values, gravity, 0.25f);
        } else if (Sensor.TYPE_MAGNETIC_FIELD == event.sensor.getType()) {
            if (calcRotationMatrix != 3) {
                calcRotationMatrix += 2;
                System.arraycopy(event.values, 0, geomag, 0, event.values.length);
            } else Utils.lowPass(geomag, event.values, geomag, 0.25f);
        }
        if (calcRotationMatrix == 3 && SensorManager.getRotationMatrix(R, null, gravity, geomag)) {
            if(changeListener!=null)
                changeListener.onOrientationChanged(R, null);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
