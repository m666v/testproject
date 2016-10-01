package com.brocorporation.cameratest;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by leon on 23.09.16.
 */

public abstract class OrientationSensor implements SensorEventListener {

    protected final SensorManager sensorManager;
    protected ChangeListener changeListener;
    public OrientationSensor(SensorManager sm){
        sensorManager = sm;
    }

    public abstract void start();
    public abstract void stop();

    public void setOnChangeListener(ChangeListener l){
        changeListener = l;
    }

    public static interface ChangeListener{
        public void onOrientationChanged(float[] R, float[] q);
    }
}
