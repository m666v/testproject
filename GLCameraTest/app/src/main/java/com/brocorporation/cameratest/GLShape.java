package com.brocorporation.cameratest;

import android.opengl.GLES20;

public abstract class GLShape {

	protected final static byte BYTES_PER_SHORT = 2, BYTES_PER_FLOAT = 4;

	public abstract void initBuffers();

	public abstract int[] getBuffers();

	public void release() {
		final int[] buffer = getBuffers();
		if (buffer != null) {
			GLES20.glDeleteBuffers(buffer.length, buffer, 0);
		}
	}
}