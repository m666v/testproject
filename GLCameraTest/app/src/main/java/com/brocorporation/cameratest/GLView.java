package com.brocorporation.cameratest;

import android.app.Activity;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by leon on 07.09.16.
 */
public class GLView extends GLSurfaceView implements GLSurfaceView.Renderer{

    private CameraTexture cameraTexture;
    private TextureOnlyShader textureOnlyShader;
    private CameraTextureShape cameraTextureShape;
    private Activity parent;
    public GLView(Activity context){
        super(context);
        parent = context;
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
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        //Log.e("mr", "Gl extensions: " + extensions);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        int texName = createTexture();
        cameraTexture = new CameraTexture(texName);
        cameraTexture.start();
        textureOnlyShader = new TextureOnlyShader(getContext());
        textureOnlyShader.initShader();
        cameraTextureShape = new CameraTextureShape(textureOnlyShader);
        cameraTextureShape.initBuffers();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(cameraTexture!=null)
            cameraTexture.stop();
    }

    public void releaseAll(){
        cameraTexture.release();
        /*GLES20.glDeleteTextures ( 1, new int[]{cameraTexture.getTexture()}, 0 );
        textureOnlyShader.release();
        cameraTextureShape.release();*/
    }



    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        cameraTexture.surfaceChanged(parent, width, height);
    }

    float[] modelMatrix = new float[16];
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        textureOnlyShader.use();
        cameraTexture.updateTexImage();
        //Matrix.setIdentityM(modelMatrix, 0);
        cameraTexture.getTransformMatrix(modelMatrix);
        cameraTextureShape.render(cameraTexture.getTexture(), modelMatrix);
    }

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
}
