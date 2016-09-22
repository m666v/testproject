package com.brocorporation.cameratest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

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
            initView();
        }

    }

    private TextView tv;
    public TextView getTextView(){
        return tv;
    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        tv = new TextView(this);
        tv.setHorizontallyScrolling(false);
        tv.setSingleLine(false);
        tv.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        tv.setMaxLines(Integer.MAX_VALUE);
        tv.setEllipsize(null);
        FrameLayout fl = new FrameLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);;
        setContentView(fl, lp);

        view = new GLView(this);
        fl.addView(view, lp);


        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);;
        tvlp.gravity = Gravity.LEFT|Gravity.TOP;
        fl.addView(tv, tvlp);

        //view = new GLView(this);
        //setContentView(view);
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
                    initView();
                } else {
                    finish();
                    System.exit(0);
                }
                break;
            }
        }
    }
}
