package com.brocorporation.cameratest;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraSurface extends SurfaceView implements
		SurfaceHolder.Callback {

	private Camera camera;
	private int cameraId;
	private Activity activity;
	public CameraSurface(Activity context) {
		super(context);
		getHolder().addCallback(this);
		activity = context;

	}

	public void surfaceCreated(SurfaceHolder holder) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		int numCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numCameras; i++) {
			Camera.getCameraInfo(i, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				if (openCamera(i)) {
					try {
						camera.setPreviewDisplay(holder);
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

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (camera != null) {
			camera.stopPreview();
			Camera.Parameters param = camera.getParameters();
			Camera.Size size = getOptimalPreviewSize(param.getSupportedPreviewSizes(), width, height);
			int orientation = getDisplayOrientation(activity, cameraId);

			param.setPreviewSize(size.width, size.height);
			param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			camera.setParameters(param);
			camera.setDisplayOrientation(orientation);
			camera.startPreview();
		}
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

	private static int getDisplayOrientation(Activity activity, int cameraId) {
		Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
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

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public void startLed() {
		Parameters param = camera.getParameters();
		param.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(param);
	}

	public void stopLed() {
		Parameters param = camera.getParameters();
		param.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(param);
	}
}
