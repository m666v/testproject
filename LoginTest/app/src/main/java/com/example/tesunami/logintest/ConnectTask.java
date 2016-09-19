package com.example.tesunami.logintest;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by tesunami on 19.09.16.
 */

public class ConnectTask extends AsyncTask<String, String, TCPClient> {
    // 0 for login, 1 for main menu, 2 for in game!
    private TCPClient tcpClient;
    private DecoderActivity currentActivity;
    public ConnectTask(DecoderActivity currentActivity) {
        this.currentActivity = currentActivity;

    }

    public DecoderActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(DecoderActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    protected TCPClient doInBackground(String... message) {

        //we create a TCPClient object and
        tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                publishProgress(message);
            }
        }, false);
        tcpClient.run();
        MainActivity.tcpClient = tcpClient;
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        currentActivity.decodeMessage(values[0]);
    }


}