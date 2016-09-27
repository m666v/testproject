package com.example.tesunami.logintest;

import android.os.AsyncTask;

/**
 * Created by tesunami on 19.09.16.
 */

public class ConnectTask extends AsyncTask<String, String, TCPClient>{
    // 0 for login, 1 for main menu, 2 for in game!
    private static TCPClient tcpClient;
    private DecoderActivity currentActivity;
    private static ConnectTask CONNEC_TTASK;
    public static boolean SERVER_ADDRESS;
    private ConnectTask(){

    }
    private ConnectTask(DecoderActivity currentActivity) {
        this.currentActivity = currentActivity;
    }


    public static ConnectTask creatConnectTask(DecoderActivity decoderActivity){
        if(ConnectTask.CONNEC_TTASK == null) {
            ConnectTask.CONNEC_TTASK = new ConnectTask(decoderActivity);
            return ConnectTask.CONNEC_TTASK;

        }
        ConnectTask.CONNEC_TTASK.currentActivity = decoderActivity;
        return ConnectTask.CONNEC_TTASK;
    }

    public static TCPClient getTcpClient() {
        return ConnectTask.tcpClient;
    }

    @Override
    protected TCPClient doInBackground(String... message) {

        //we create a TCPClient object and

             ConnectTask.tcpClient = TCPClient.creatTCPClient(new TCPClient.OnMessageReceived(){
                public void messageReceived(String message) {
                    publishProgress(message);

                }
            },SERVER_ADDRESS);
            tcpClient.start();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        currentActivity.decodeMessage(values[0]);
    }


}