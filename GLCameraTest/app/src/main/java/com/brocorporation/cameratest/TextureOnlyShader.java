package com.brocorporation.cameratest;

import android.content.Context;

/**
 * Created by leon on 07.09.16.
 */
public class TextureOnlyShader extends GLShader {

    private static String attributes[] = { "a_Position", "a_UV" };
    private static String uniform[] = { "u_Texture", "u_Model" };
    public static byte A_POSITION = 0;
    public static byte A_UV = 1;
    public static byte U_TEXTURE = 0;
    public static byte U_MODEL = 1;

    public TextureOnlyShader(Context context) {
        super(context, "textureonly.vsh", "textureonly.fsh");
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
