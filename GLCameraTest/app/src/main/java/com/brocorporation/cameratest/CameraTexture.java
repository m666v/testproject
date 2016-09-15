package com.brocorporation.cameratest;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;

import java.io.IOException;
import java.util.List;

/**
 * Created by leon on 07.09.16.
 */
public class CameraTexture extends SurfaceTexture {
    private Camera camera;
    private int texName, cameraId;

    public CameraTexture(int texName) {
        super(texName);
        this.texName = texName;
    }

    private boolean openCamera(int id) {
        try {
            releaseCamera();
            camera = Camera.open(id);
            return camera != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void releaseCamera() {

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public void start() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                if (openCamera(i)) {
                    try {
                        camera.setPreviewTexture(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    camera.startPreview();
                }
                cameraId = i;
                break;
            }
        }

    }

    public void stop() {
        if (camera != null) {
            camera.stopPreview();
            releaseCamera();
        }
    }

    public void surfaceChanged(int width, int height, int rotation) {
        if (camera == null) return;
        camera.stopPreview();
        Camera.Parameters param = camera.getParameters();
        Camera.Size size = getOptimalPreviewSize(param.getSupportedPreviewSizes(), width, height);
        int orientation = getDisplayOrientation(rotation, cameraId);

        param.setPreviewSize(size.width, size.height);
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        camera.setParameters(param);
        camera.setDisplayOrientation(orientation);
        camera.startPreview();
    }

    private static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        if (sizes == null) return null;
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private static Camera.Size getMaxPreviewSize(List<Camera.Size> list) {
        int width = 0;
        int height = 0;
        Camera.Size maxSize = null;
        for (Camera.Size size : list) {
            if (size.width * size.height > width * height)
            {
                width = size.width;
                height = size.height;
                maxSize = size;
            }
        }
        return maxSize;
    }

    private static int getDisplayOrientation(int rotation, int cameraId) {
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }


    public int getTexture() {
        return texName;
    }

    public Camera getCamera() {
        return camera;
    }
}
