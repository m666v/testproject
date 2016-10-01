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

    public static void geoToCart(float[] out, double latitude, double longitude){
        double radLat = Math.toRadians(latitude);
        double radLng = Math.toRadians(longitude);
        double clat = Math.cos(radLat);
        out[0] = (float)(6371000 *  clat * Math.cos(radLng));
        out[1] = (float)(6371000 * Math.sin(radLat));
        out[2] = (float)(6371000 *  clat * Math.sin(radLng));
    }

    public static void CartToGeo(double[] out, float x, float y, float z){
        out[0] = Math.asin(y / 6371000);
        out[1]  = Math.atan2(z, x);
    }

    public static void localDifCart(float[] out, double[] p1, double[] p2){
        localDifCart(out, p1[0], p1[1], p2[0], p2[1]);
    }

    public static void localDifCart(float[] out, double lat1, double lon1, double lat2, double lon2){
        out[0] = (float)(111300 * (lon2 - lon1) * Math.cos(Math.toRadians((lat1 + lat2) / 2)));
        out[1] = (float)(111300 * (lat2 - lat1));
    }
}
