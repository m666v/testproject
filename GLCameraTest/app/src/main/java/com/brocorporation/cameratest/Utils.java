package com.brocorporation.cameratest;

/**
 * Created by leon on 23.09.16.
 */

public class Utils {
    public static void lowPass(float[] out, float[] from, float[] to, float alpha) {
        for (int i = out.length - 1; i >= 0; i--) {
            out[i] = to[i] + (from[i] - to[i]) * alpha;
        }
    }
}
