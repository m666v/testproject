package com.example.tesunami.logintest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class MainActivity extends AppCompatActivity implements DecoderActivity {
    private static final String[] ITEMS = {"LeonServer", "MohammadServer"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Server Selection
        final Spinner dropdown = (Spinner) findViewById(R.id.serverListSP);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ITEMS);
        dropdown.setAdapter(adapter);
        Button selectButton = (Button) findViewById(R.id.selectBTN);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropdown.getSelectedItem().toString().equals("LeonServer")){
                    Log.e("Main","Leon");
                    ConnectTask.SERVER_ADDRESS = true;}
                else {
                    Log.e("Main","Mohammad");
                    ConnectTask.SERVER_ADDRESS = false;
                }
                runLoginActivity();

            }

        });


    }
    private void runLoginActivity(){
        ConnectTask.creatConnectTask(this).execute("");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void decodeMessage(String message) {
        return;
    }


}