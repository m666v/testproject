package com.example.tesunami.logintest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements DecoderActivity {

    public static TCPClient tcpClient;
    private ConnectTask connectTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //login proccess
        if (true) {

            connectTask = new ConnectTask(this);
            Intent intent= new Intent(this,LoginActivity.class);
            intent.putExtra("ConnectTask", (Serializable) connectTask);
            startActivity(intent);
        } else {

        }

    }


    @Override
    public void decodeMessage(String message) {

        return;
    }


}