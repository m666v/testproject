package com.example.tesunami.logintest;

import android.os.AsyncTask;


/**
 * Created by tesunami on 19.09.16.
 */

public class ConnectTask extends AsyncTask<String, String, TCPClient> {
    // 0 for login, 1 for main menu, 2 for in game!
    private static TCPClient tcpClient;
    private DecoderActivity currentActivity;
    private static ConnectTask connectTask;

    private ConnectTask(){

    }
    private ConnectTask(DecoderActivity currentActivity) {
        this.currentActivity = currentActivity;
    }


    public static ConnectTask creatConnectTask(DecoderActivity decoderActivity){
        if(ConnectTask.connectTask == null)
            return new ConnectTask(decoderActivity);
        return ConnectTask.connectTask;
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
        if (tcpClient == null) {
            tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            }, false);

            MainActivity.tcpClient = tcpClient;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        currentActivity.decodeMessage(values[0]);
    }

}