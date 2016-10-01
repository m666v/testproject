package com.example.tesunami.logintest;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity implements DecoderActivity{
    private ConnectTask connectTask;
    TextView CHAR1;
    TextView CHAR2;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectTask = ConnectTask.creatConnectTask(this);
        connectTask.getTcpClient().sendMessage("01#MainMenu\n");
        setContentView(R.layout.activity_main_menu);
        progressDialog = ProgressDialog.show(MainMenuActivity.this, "","Loading charachter layout", true);
        this.CHAR1 =(TextView) findViewById(R.id.char1TV);
        this.CHAR2 = (TextView) findViewById(R.id.char2TV);


    }


    @Override
    public void decodeMessage(String message) {
        if(message.split("#")[0].equals("01")){
            if(message.split("#")[1].equals("01")){
                this.CHAR1.setText(message.split("#")[2]);
            }else if(message.split("#")[1].equals("02")){
                this.CHAR2.setText(message.split("#")[2]);
            }else {
                progressDialog.dismiss();
            }
        }
    }
}
