package com.brocorporation.cameratest;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by leon on 07.09.16.
 */
public class CubeShape extends GLShape {

    private final static byte POS_PER_VERTEX = 3;
    private final static byte COL_PER_VERTEX = 3;
    private final static byte STRIDE = (POS_PER_VERTEX + COL_PER_VERTEX) * BYTES_PER_FLOAT;

    private ColorShader shader;
    private final FloatBuffer vBuffer;
    private final ShortBuffer iBuffer;
    int[] buffers = new int[2];

    public CubeShape(ColorShader shader) {
        super();
        this.shader = shader;
        float[] vertices = new float[]{
                -0.5f,-0.5f,-0.5f,0,0,0,
                +0.5f,-0.5f,-0.5f,1,0,0,
                -0.5f,+0.5f,-0.5f,0,1,0,
                +0.5f,+0.5f,-0.5f,1,1,0,
                -0.5f,-0.5f,+0.5f,0,0,1,
                +0.5f,-0.5f,+0.5f,1,0,1,
                -0.5f,+0.5f,+0.5f,0,1,1,
                +0.5f,+0.5f,+0.5f,1,1,1
                };
        short[] indices = new short[]{
                0, 2, 3, 0, 3, 1,
                1, 3, 7, 1, 7, 5,
                5, 7, 6, 5, 6, 4,
                4, 6, 2, 4, 2, 0,
                2, 6, 7, 2, 7, 3,
                4, 0, 1, 4, 1, 5};
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

    public void render(float[] mvpMatrix) {
        final int[] aHandle = shader.aHandle;
        final int[] uHandle = shader.uHandle;
        final int positionHandle = aHandle[ColorShader.A_POSITION];
        final int colorHandle = aHandle[ColorShader.A_COLOR];
        GLES20.glUniformMatrix4fv(uHandle[ColorShader.U_MVP],1, false, mvpMatrix, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(positionHandle, POS_PER_VERTEX, GLES20.GL_FLOAT, false, STRIDE, 0);
        GLES20.glVertexAttribPointer(colorHandle, COL_PER_VERTEX, GLES20.GL_FLOAT, false, STRIDE, 12);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }

    @Override
    public int[] getBuffers() {
        return buffers;
    }
}
