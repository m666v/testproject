
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class ServerThread extends Thread {
    private int errorNumber;
    private final Socket socket;
    InputStream inputStream;
    BufferedReader bufferReader;
    DataOutputStream out;
    private String lastSentMsg;
    private String lastRecievedMsg;
    private String clientMessage;
    private ClientStatus clientStatus = ClientStatus.OFFLINE;
    
    //constructor
    public ServerThread(Socket socket){
        this.socket = socket;
    }
    
    
    
    @Override
    public void run(){
        
        try{
            inputStream = socket.getInputStream();
            bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            out = new DataOutputStream(socket.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
            return;
        }
        try{
            clientMessage = bufferReader.readLine();
            if(clientMessage == null){
                socket.close();
                return;
            }
        switch(clientStatus){
            case OFFLINE:
                decodeOffline();
            case LOGIN_IN:
                decodeLogginIn();
            case MAIN_MENU:
                decodeMainMenu();
            case IN_GAME:
                decodeInGame();
        }
        
                
        
                  
        }catch (IOException e){
            e.printStackTrace();
            return;
        }
    }
    
    
    private void decodeOffline(){
        // 00 code is for login only and only!!!
        if(this.clientMessage.split("#")[0].equals("00")){
            String username = this.clientMessage.split("#")[1];
            String pass = this.clientMessage.split("#")[2];
            
        }
    }
    private void decodeLogginIn(){
        
    }
    private void decodeMainMenu(){
        
    }
    private void decodeInGame(){
        
    }
    public void messageDecoder(String message) throws IOException{

        out.writeBytes(message);
        out.flush();  
    }
    
    
    
}
