package com.brocorporation.cameratest;

import android.content.Context;

/**
 * Created by leon on 07.09.16.
 */
public class ColorShader extends GLShader {

    private static String attributes[] = { "a_Position", "a_Color" };
    private static String uniform[] = { "u_MVP" };
    public static byte A_POSITION = 0;
    public static byte A_COLOR = 1;
    public static byte U_MVP = 0;

    public ColorShader(Context context) {
        super(context, "simple.vsh", "simple.fsh");
    }

    @Override
    public void initShader() {
        super.initShader();
    }

    @Override
    public String[] getAttributes() {
        return attributes;
    }

    @Override
    public String[] getUniforms() {
        return uniform;
    }
}
