package com.example.tesunami.logintest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by tesunami on 17.09.16.
 */
public class TCPClient extends Thread{
    private final String SERVERIP;
    public static final int SERVERPORT = 4444;
    private PrintWriter out;
    private BufferedReader in;
    private OnMessageReceived mMessageListener = null;
    private String serverMessage;
    private boolean mRun;
    private boolean update;
    private boolean updateDone;
    private int updateLen;
    private static TCPClient SINGLETON;
    byte[] arrayByte;
    int offset;

    public static TCPClient creatTCPClient(OnMessageReceived listener, boolean serverID){
        if(TCPClient.SINGLETON == null){
            TCPClient.SINGLETON = new TCPClient(listener, serverID);
            return TCPClient.SINGLETON;
        }else
            return TCPClient.SINGLETON;
    }

    public TCPClient(OnMessageReceived listener, boolean serverID) {
        mMessageListener = listener;
        if(serverID){
            SERVERIP = "192.168.0.11";
        }else {
            SERVERIP = "192.168.178.40";
        }
        TCPClient.SINGLETON = this;
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient() {
        mRun = false;
    }

    @Override
    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //sending log message so u know that u r connected!
                Log.e("TCP Client", "C: Sent.");

                Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                InputStream inputStream=socket.getInputStream();
                in = new BufferedReader(new InputStreamReader(inputStream));

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    if(!update){
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        Log.e("TCP Client","C: Recieved.");
                        mMessageListener.messageReceived(serverMessage);

                    }
                    serverMessage = null;
                    }else{
                        arrayByte = new byte[updateLen];
                        if(updateDone){
                            giveBackUpdate(arrayByte);
                        }else{
                            offset = inputStream.read(arrayByte, offset, arrayByte.length);
                            if(offset == updateLen){
                                updateDone = true;
                            }
                            offset++;
                        }
                    }

                }



            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    private void giveBackUpdate(byte[] b)  {
        LoginActivity.UpdateDB(b);
        this.update = false;
    }

    public void callUpdate(int updateLen){
        this.updateLen = updateLen;
        this.update = true;
        sendMessage("00#02#rdy for Copy Database");
    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
