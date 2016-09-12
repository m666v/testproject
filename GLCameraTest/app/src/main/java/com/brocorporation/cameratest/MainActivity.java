package com.brocorporation.cameratest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class MainActivity extends Activity {
    private final static int PERMISSIONS_REQUEST_CAMERA = 1;
    GLView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        }else{
            initCameraView();
        }

    }

    private void initCameraView() {
       view = new GLView(this);
       setContentView(view);
        //setContentView(new CameraSurface(this));
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(view != null){
            view.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(view!=null){
            view.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(view!=null) {
            view.releaseAll();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCameraView();
                } else {
                    finish();
                    System.exit(0);
                }
                break;
            }
        }
    }
}
