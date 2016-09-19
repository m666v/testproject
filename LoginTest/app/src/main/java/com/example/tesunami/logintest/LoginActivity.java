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

public class LoginActivity extends AppCompatActivity implements DecoderActivity{

    private static final String[] ITEMS = {"LeonServer", "MohammadServer"};
    private ConnectTask connectTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        this.connectTask =(ConnectTask) intent.getSerializableExtra("connectTask");
        connectTask.setCurrentActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = (Button) findViewById(R.id.loginBTN);
        final EditText username = (EditText) findViewById(R.id.usernameET);
        final EditText pass = (EditText) findViewById(R.id.passwordP);

        //dropdown itemz
        Spinner dropdown = (Spinner) findViewById(R.id.serverListSP);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ITEMS);
        dropdown.setAdapter(adapter);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((username.getText().equals("")) && (pass.getText().equals(""))) {
                    String message = "00#" + username.getText().toString() + "#" + pass.getText().toString()+"\n";
                    if (MainActivity.tcpClient != null) {
                        MainActivity.tcpClient.sendMessage(message);
                        ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "",
                                "Connecting to server...", true);
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Wrong Entery");
                    alertDialog.setMessage("You need to enter username and password!!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });


    }

    @Override
    public void decodeMessage(String message) {
        //login proccess
        if(message.split("#")[0].equals("00")){
        Intent intent= new Intent(this,LoginActivity.class);
        intent.putExtra("ConnectTask", (Serializable) connectTask);
        startActivity(intent);
        }

    }


}