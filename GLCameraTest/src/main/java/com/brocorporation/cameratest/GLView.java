package com.brocorporation.cameratest;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.Surface;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by leon on 07.09.16.
 */
public class GLView extends GLSurfaceView implements GLSurfaceView.Renderer, OrientationSensor.ChangeListener, LocationSensor.ChangeListener {

    private CameraTexture cameraTexture;
    private TextureOnlyShader textureOnlyShader;
    private CameraTextureShape cameraTextureShape;
    private Activity context;
    private final OrientationSensor orientationSensor;
    private final LocationSensor locationSensor;


    public GLView(Activity context) {
        super(context);
        this.context = context;

        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            orientationSensor = new RVOrientationSensor(sm);
        } else {
            orientationSensor = new AMOrientationSensor(sm);
        }
        orientationSensor.setOnChangeListener(this);
        locationSensor = new LocationSensor(context);
        locationSensor.setOnChangeListener(this);

        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_CONTINUOUSLY);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preTranslate = !preTranslate;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        orientationSensor.start();
        locationSensor.start();
        Matrix.setIdentityM(R, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraTexture != null)
            cameraTexture.stop();
        orientationSensor.stop();
        locationSensor.stop();
    }

    public void releaseAll() {
        cameraTexture.release();
        /*GLES20.glDeleteTextures ( 1, new int[]{cameraTexture.getTexture()}, 0 );
        textureOnlyShader.release();
        cameraTextureShape.release();*/
    }

    private final float[] projectionMatrix = new float[16], viewMatrix = new float[16], modelMatrix = new float[16], mv = new float[16], mvp = new float[16], vp = new float[16], tmp = new float[16];

    private ColorShader colorShader;
    private CubeShape cubeShape;
    private final static double[] refPoint = {50.70246, 7.080825};
    private final float[][] points = calcPoints();

    public static float[][] calcPoints(){
        float[] ippHof = new float[2],
                busIpp = new float[2],
                busPop = new float[2],
                busHeu = new float[2],
                rigWie = new float[2],
                domHof = new float[2];
        Utils.localDifCart(ippHof,50.70245, 7.080711, refPoint[0], refPoint[1]);
        Utils.localDifCart(busIpp,50.70395, 7.082050, refPoint[0], refPoint[1]);
        Utils.localDifCart(busPop,50.72166, 7.088126, refPoint[0], refPoint[1]);
        Utils.localDifCart(busHeu,50.71778, 7.124773, refPoint[0], refPoint[1]);
        Utils.localDifCart(rigWie,50.67885, 7.157743, refPoint[0], refPoint[1]);
        Utils.localDifCart(domHof,50.62539, 7.0201121, refPoint[0], refPoint[1]);
        return new float[][]{ippHof, busIpp,busPop,busHeu,rigWie,domHof};
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
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
        final int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
        cameraTexture.surfaceChanged(width, height, rotation);
        calculateMappingAxis(rotation);
        final float aspect = (float) width / height;
        float fovy = (float) Math.tan(Math.toRadians(cameraTexture.getCamera().getParameters().getVerticalViewAngle() / 2));
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            fovy /= aspect;
        }
        GLES20.glViewport(0, 0, width, height);
        final float near = 0.01f;
        final float t = near * fovy;
        final float r = t * aspect;
        Matrix.frustumM(projectionMatrix, 0, -r, r, -t, t, near, 50);
    }

    float angle1 = 0, angle2 = 0;
    long lastTime = System.currentTimeMillis();
    boolean preTranslate = true;
    float[] tmpPoint = new float[2];
    StringBuilder sb = new StringBuilder();

    @Override
    public void onDrawFrame(GL10 gl) {
        long currentTime = System.currentTimeMillis();
        float factor = (currentTime - lastTime) / 1000f;
        lastTime = currentTime;

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        textureOnlyShader.use();
        cameraTexture.updateTexImage();
        cameraTexture.getTransformMatrix(modelMatrix);
        cameraTextureShape.render(cameraTexture.getTexture(), modelMatrix);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);

        Camera.Parameters params = cameraTexture.getCamera().getParameters();
        sb.setLength(0);
        sb.append(String.format("%4f %4f %4f", viewDirection[0], viewDirection[1], viewDirection[2]));
        sb.append('\t').append(params.getHorizontalViewAngle()).append('\t').append(params.getVerticalViewAngle());
        sb.append('\t').append(params.getFocalLength());
        sb.append('\t').append(cameraTexture.getCameraInfo().orientation);
        sb.append('\n').append(params.getPreviewSize().width + "\t" + params.getPreviewSize().height);
        sb.append('\t').append(preTranslate);


        if (cameraLocation != null) {
            synchronized (cameraPoint){
                Utils.localDifCart(tmpPoint, cameraPoint, refPoint);
                sb.append('\n').append(String.format("%6f %6f (%4f)", cameraPoint[0], cameraPoint[1], cameraLocation.getAccuracy()));
                sb.append('\n').append(String.format("%4f 0 %4f", tmpPoint[0], tmpPoint[1]));
            }

            colorShader.use();
            Matrix.setIdentityM(tmp, 0);
            Matrix.translateM(tmp, 0, -tmpPoint[0], -1.7f, -tmpPoint[1]);
            synchronized (R) {
                Matrix.multiplyMM(viewMatrix, 0, R, 0, tmp, 0);
            }
            if (preTranslate) Matrix.translateM(viewMatrix, 0, camOffsetX, camOffsetY, 0);

            Matrix.multiplyMM(vp, 0, projectionMatrix, 0, viewMatrix, 0);


            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, tmpPoint[0] - 3, 0 + 0.15f, tmpPoint[1] - 2f);
            Matrix.rotateM(modelMatrix, 0, angle1, 0, 1, 0);
            angle1 += 30 * factor;
            Matrix.multiplyMM(mvp, 0, vp, 0, modelMatrix, 0);
            cubeShape.render(mvp);

            for(float[] p : points){
                Matrix.setIdentityM(modelMatrix, 0);
                Matrix.translateM(modelMatrix, 0, p[0], 0.5f, p[1]);
                Matrix.rotateM(modelMatrix, 0, angle2, 0, 1, 0);
                Matrix.multiplyMM(mvp, 0, vp, 0, modelMatrix, 0);
                cubeShape.render(mvp);
            }
            angle2 += 45 * factor;
        }
        ((MainActivity) context).getTextView().post(new Runnable(){
            @Override
            public void run() {
                ((MainActivity) context).getTextView().setText(sb.toString());
            }
        });
    }

    private static int createTexture() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        return texture[0];
    }


    float[] R = new float[16];
    float[] viewDirection = new float[3];

    @Override
    public void onOrientationChanged(float[] inR, float[] q) {
        synchronized (R) {
            SensorManager.remapCoordinateSystem(inR, xAxis, yAxis, R);
            float x = R[4], y = R[5], z = R[6];
            R[4] = R[8];
            R[5] = R[9];
            R[6] = R[10];
            R[8] = -x;
            R[9] = -y;
            R[10] = -z;
            //viewDirection[0] = R[2];
            //viewDirection[1] = R[6];
            //viewDirection[2] = R[10];
            viewDirection[0] = -R[8];
            viewDirection[1] = -R[9];
            viewDirection[2] = -R[10];
        }
    }

    private Location cameraLocation;
    private final double[] cameraPoint = new double[2];

    @Override
    public void onLocationChanged(Location location) {
        synchronized (cameraPoint){
            cameraLocation = location;
            cameraPoint[0] = location.getLatitude();
            cameraPoint[1] = location.getLongitude();
        }
    }

    int xAxis, yAxis;
    float camOffsetX, camOffsetY;

    public void calculateMappingAxis(int rotation) {
        switch (rotation) {
            case Surface.ROTATION_0:
                xAxis = SensorManager.AXIS_X;
                camOffsetX = 0;
                yAxis = SensorManager.AXIS_Y;
                camOffsetY = 0.052f;
                break;
            case Surface.ROTATION_90:
                xAxis = SensorManager.AXIS_Y;
                camOffsetX = 0.052f;
                yAxis = SensorManager.AXIS_MINUS_X;
                camOffsetY = 0f;
                break;
            case Surface.ROTATION_180:
                xAxis = SensorManager.AXIS_MINUS_X;
                camOffsetX = 0;
                yAxis = SensorManager.AXIS_MINUS_Y;
                camOffsetY = -0.052f;
                break;
            case Surface.ROTATION_270:
                xAxis = SensorManager.AXIS_MINUS_Y;
                camOffsetX = -0.052f;
                yAxis = SensorManager.AXIS_X;
                camOffsetY = 0;
                break;
        }
    }


}
