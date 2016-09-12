package com.brocorporation.cameratest;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by leon on 07.09.16.
 */
public abstract class GLShader {

    protected final int[] aHandle;
    protected final int[] uHandle;

    protected final String vertexShaderCode;
    protected final String fragmentShaderCode;
    protected int program;

    public GLShader(Context context, String vsh, String fsh) {
        aHandle = new int[getAttributes().length];
        uHandle = new int[getUniforms().length];
        vertexShaderCode = readShader(context, vsh);
        fragmentShaderCode = readShader(context, fsh);
    }

    public void initShader() {
        final String[] attributes = getAttributes();
        final String[] uniforms = getUniforms();
        final int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        final int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        program = createAndLinkProgram(vertexShader, fragmentShader, attributes);

        use();
        for (int i = attributes.length - 1; i >= 0; i--) {
            aHandle[i] = GLES20.glGetAttribLocation(program, attributes[i]);
        }
        for (int i = uniforms.length - 1; i >= 0; i--) {
            uHandle[i] = GLES20.glGetUniformLocation(program, uniforms[i]);
        }
    }

    public void use() {
        GLES20.glUseProgram(getProgram());
    }

    public int getProgram() {
        return program;
    }

    public int[] getAttributeHandles() {
        return aHandle;
    }

    public int[] getUniformHandles() {
        return uHandle;
    }

    public abstract String[] getAttributes();

    public abstract String[] getUniforms();

    public void release(){
        GLES20.glDeleteProgram(program);
    }

    protected static String readShader(Context context, String filename) {
        BufferedReader bufferedReader = null;
        final StringBuilder body = new StringBuilder();
        String nextLine;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
            while ((nextLine = bufferedReader.readLine()) != null)
                body.append(nextLine).append('\n');
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
            }
        }
        return body.toString();
    }

    protected static int compileShader(final int shaderType,
                                       final String shaderSource) {
        int shaderHandle = GLES20.glCreateShader(shaderType);
        if (shaderHandle != 0) {
            GLES20.glShaderSource(shaderHandle, shaderSource);
            GLES20.glCompileShader(shaderHandle);
            int[] result = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, result, 0);
            if (result[0]==GLES20.GL_FALSE) {
                System.out.println(shaderSource);
                System.err.println("ShaderHelper, Error compiling shader: "
                        + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0) {
            throw new RuntimeException("Error creating shader.");
        }
        return shaderHandle;
    }

    protected static int createAndLinkProgram(final int vertexShaderHandle,
                                              final int fragmentShaderHandle, final String[] attributes) {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            if (attributes != null) {
                final int size = attributes.length;
                for (int i = 0; i < size; i++) {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            GLES20.glLinkProgram(programHandle);
            GLES20.glDeleteShader(vertexShaderHandle);
            GLES20.glDeleteShader(fragmentShaderHandle);
            int[] result = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, result, 0);
            if (result[0]==GLES20.GL_FALSE) {
                System.err.println("ShaderHelper, Error compiling program: "
                        + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }
        return programHandle;
    }
}