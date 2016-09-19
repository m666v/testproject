package com.example.tesunami.logintest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity implements DecoderActivity{
    private ConnectTask connectTask;
    TextView CHAR1;
    TextView CHAR2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectTask = ConnectTask.creatConnectTask(this);
        connectTask.setCurrentActivity(this);

        setContentView(R.layout.activity_main_menu);
        this.CHAR1 =(TextView) findViewById(R.id.char1TV);
        this.CHAR2 = (TextView) findViewById(R.id.char2TV);
        MainActivity.tcpClient.sendMessage("01#MainMenu\n");

    }


    @Override
    public void decodeMessage(String message) {
        if(message.split("#")[0].equals("01")){
            if(message.split("#")[1].equals("01")){
                this.CHAR1.setText(message.split("#")[2]);
            }else{
                this.CHAR2.setText(message.split("#")[2]);
            }
        }
    }
}
