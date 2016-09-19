package com.example.tesunami.logintest;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements DecoderActivity {

    public static TCPClient tcpClient;
    private ConnectTask connectTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //login proccess
        if (true) {

            connectTask = ConnectTask.creatConnectTask(this);
            Intent intent= new Intent(this,LoginActivity.class);
            startActivity(intent);
        } else {

        }

    }


    @Override
    public void decodeMessage(String message) {

        return;
    }


}