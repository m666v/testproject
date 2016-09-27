package com.example.tesunami.logintest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity implements DecoderActivity{

    private ConnectTask connectTask;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connectTask = ConnectTask.creatConnectTask(this);
        Button login = (Button) findViewById(R.id.loginBTN);
        final EditText username = (EditText) findViewById(R.id.usernameET);
        final EditText pass = (EditText) findViewById(R.id.passwordP);
        Log.e("Login","inja");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((username.getText().length()>0) && (pass.getText().length()>0)) {

                    String message = "00#" + username.getText().toString() + "#" + pass.getText().toString()+"\n";

                    if (ConnectTask.getTcpClient() != null) {
                        ConnectTask.getTcpClient().sendMessage(message);
                        progressDialog = ProgressDialog.show(LoginActivity.this, "","Connecting to server...", true);
                    }
                } else {
                    AlertDialog alertDialog= new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Wrong Entery" + username.getText() + pass.getText());
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
        Log.e("LoginTest","Decoded?");
        if(message.split("#")[0].equals("00")){
            progressDialog.dismiss();
        Intent intent= new Intent(this,MainMenuActivity.class);
        startActivity(intent);
        }

    }


}