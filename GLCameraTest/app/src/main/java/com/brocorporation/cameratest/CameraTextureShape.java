package com.brocorporation.cameratest;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by leon on 07.09.16.
 */
public class CameraTextureShape extends GLShape {

    private final static byte POS_PER_VERTEX = 2;
    private final static byte UV_PER_VERTEX = 2;
    private final static byte STRIDE = (POS_PER_VERTEX + UV_PER_VERTEX) * BYTES_PER_FLOAT;

    private TextureOnlyShader shader;
    private final FloatBuffer vBuffer;
    private final ShortBuffer iBuffer;
    int[] buffers = new int[2];

    public CameraTextureShape(TextureOnlyShader shader) {
        super();
        this.shader = shader;
        float[] vertices = new float[]{-1, 1, 1, 1, -1, -1, 1, 0, 1, -1, 0, 0, 1, 1, 0, 1};
        short[] indices = new short[]{0, 1, 2, 0, 2, 3};
        vBuffer = ByteBuffer
                .allocateDirect(vertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vBuffer.put(vertices).flip();
        iBuffer = ByteBuffer
                .allocateDirect(indices.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        iBuffer.put(indices).flip();
    }

    @Override
    public void initBuffers() {
        GLES20.glGenBuffers(2, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vBuffer.capacity()*BYTES_PER_FLOAT, vBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, iBuffer.capacity()*BYTES_PER_SHORT, iBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void render(int texture, float[] modelMatrix) {
        final int[] aHandle = shader.aHandle;
        final int[] uHandle = shader.uHandle;
        final int positionHandle = aHandle[TextureOnlyShader.A_POSITION];
        final int uvHandle = aHandle[TextureOnlyShader.A_UV];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
        GLES20.glUniform1i(uHandle[TextureOnlyShader.U_TEXTURE], 0);
        GLES20.glUniformMatrix4fv(uHandle[TextureOnlyShader.U_MODEL],1, false, modelMatrix, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(uvHandle);
        GLES20.glVertexAttribPointer(positionHandle, POS_PER_VERTEX, GLES20.GL_FLOAT, false, STRIDE, 0);
        GLES20.glVertexAttribPointer(uvHandle, UV_PER_VERTEX, GLES20.GL_FLOAT, false, STRIDE, 8);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(uvHandle);
    }

    @Override
    public int[] getBuffers() {
        return buffers;
    }
}
