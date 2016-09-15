package com.brocorporation.cameratest;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.Surface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by leon on 07.09.16.
 */
public class GLView extends GLSurfaceView implements GLSurfaceView.Renderer, SensorEventListener{

    private CameraTexture cameraTexture;
    private TextureOnlyShader textureOnlyShader;
    private CameraTextureShape cameraTextureShape;
    private Activity context;
    private final SensorManager sensorManager;
    private final Sensor accelerometer, magnetometer;


    public GLView(Activity context){
        super(context);
        this.context = context;

        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_CONTINUOUSLY);

        /*final ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        Log.e("support", supportsEs2+"_");*/

    }


    @Override
    public void onResume(){
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        Matrix.setIdentityM(R,0);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(cameraTexture!=null)
            cameraTexture.stop();
        sensorManager.unregisterListener(this);
    }

    public void releaseAll(){
        cameraTexture.release();
        /*GLES20.glDeleteTextures ( 1, new int[]{cameraTexture.getTexture()}, 0 );
        textureOnlyShader.release();
        cameraTextureShape.release();*/
    }

    private final float[] projectionMatrix = new float[16]
            , viewMatrix= new float[16]
            , modelMatrix = new float[16]
            , mv = new float[16]
            , mvp = new float[16], vp = new float[16], tmp = new float[16];

    private ColorShader colorShader;
    private CubeShape cubeShape;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        //Log.e("onSurfaceCreated", "Gl extensions: " + extensions);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);


        int texName = createTexture();
        cameraTexture = new CameraTexture(texName);
        cameraTexture.start();
        textureOnlyShader = new TextureOnlyShader(getContext());
        textureOnlyShader.initShader();
        cameraTextureShape = new CameraTextureShape(textureOnlyShader);
        cameraTextureShape.initBuffers();
        colorShader = new ColorShader(getContext());
        colorShader.initShader();
        cubeShape = new CubeShape(colorShader);
        cubeShape.initBuffers();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        final float near = 0.01f;
        final float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix,0,-ratio*near,ratio*near,-1*near,1*near,near,50);
        final int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
        cameraTexture.surfaceChanged(width, height, rotation);
        calculateMappingAxis(rotation);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        textureOnlyShader.use();
        cameraTexture.updateTexImage();
        cameraTexture.getTransformMatrix(modelMatrix);
        cameraTextureShape.render(cameraTexture.getTexture(), modelMatrix);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        colorShader.use();
        Matrix.setIdentityM(viewMatrix, 0);//
        //synchronized (this) {
        //    System.arraycopy(R,0, viewMatrix,0, 16);
        //}
        //Matrix.translateM(viewMatrix,0,0,-1.7f*0,0);

        Matrix.setIdentityM(viewMatrix,0);
        //Matrix.translateM(viewMatrix,0,0,0,0);
        synchronized (this) {
            System.arraycopy(R,0,viewMatrix,0,16);//TODO getOrientation rotate in right order
        }


        Matrix.multiplyMM(vp, 0, projectionMatrix, 0, viewMatrix, 0);

        Matrix.setIdentityM(modelMatrix, 0);

        /* Matrix.setIdentityM(tmp,0);
        Matrix.translateM(tmp,0,0,0,-8);
        synchronized (this) {
             Matrix.multiplyMM(modelMatrix,0,tmp,0,R,0);
        }*/
        Matrix.translateM(modelMatrix,0, 0, 0.0f,-2f);


        Matrix.multiplyMM(mvp,0,vp, 0, modelMatrix,0);

        cubeShape.render(mvp);
        a+=30/60f;
    }float a=0;

    private static int createTexture() {
        int[] texture = new int[1];
        GLES20.glGenTextures ( 1, texture, 0 );
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        return texture[0];
    }

    final float[] gravity = new float[3],geomag = new float[3];
    byte calcRotationMatrix = 0;
    final float[] inR = new float[16], R = new float[16];
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            return;
        if(Sensor.TYPE_ACCELEROMETER == event.sensor.getType()){
            if(calcRotationMatrix!=3) {
                calcRotationMatrix += 1;
                System.arraycopy(event.values,0,gravity,0,event.values.length);
            }else lowPass(gravity, event.values, gravity, 0.8f);
        }else if(Sensor.TYPE_MAGNETIC_FIELD == event.sensor.getType()){
            if(calcRotationMatrix!=3){
                calcRotationMatrix+=2;
                System.arraycopy(event.values,0,geomag,0,event.values.length);
            }else lowPass(geomag, event.values, geomag, 0.8f);
        }
        if(calcRotationMatrix==3 && SensorManager.getRotationMatrix(inR, null, gravity, geomag)){
            synchronized (this) {
                SensorManager.remapCoordinateSystem(inR, xAxis, yAxis, R);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    int xAxis, yAxis;
    public void calculateMappingAxis(int rotation){
        switch (rotation) {
            case Surface.ROTATION_0:
                xAxis = SensorManager.AXIS_X;
                yAxis = SensorManager.AXIS_Y;
                break;
            case Surface.ROTATION_90:
                xAxis = SensorManager.AXIS_Y;
                yAxis = SensorManager.AXIS_MINUS_X;
                break;
            case Surface.ROTATION_180:
                xAxis = SensorManager.AXIS_MINUS_X;
                yAxis = SensorManager.AXIS_MINUS_Y;
                break;
            case Surface.ROTATION_270:
                xAxis = SensorManager.AXIS_MINUS_Y;
                yAxis = SensorManager.AXIS_X;
                break;
        }
    }

    public static void lowPass(float[] out, float[] from, float[] to, float alpha){
        for(int i=out.length-1;i>=0;i--){
            out[i]=from[i]+(to[i]-from[i])*alpha;
        }
    }
}
