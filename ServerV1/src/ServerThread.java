
import java.beans.Statement;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends Thread {

    private int errorNumber;
    private final Socket socket;
    private InputStream inputStream;
    private BufferedReader br;
    private DataOutputStream out;
    private String lastSentMsg;
    private String lastRecievedMsg;
    private String clientMessage;
    private ClientStatus clientStatus = ClientStatus.OFFLINE;
    private Connection c;
    private Player player;
    private File DB = new File("/home/tesunami/Git projects/testproject/ClientDB.sqlite");;

    //constructor
    public ServerThread(Socket socket, Player player) {
        this.socket = socket;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            c = DriverManager.getConnection("jdbc:derby://localhost:1527/GameDB");
            inputStream = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(inputStream));
            out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                //to jump out of while block and finish the thread!
                clientMessage = br.readLine();
                if (this.isInterrupted() || (clientMessage == null)) {
                    //finally will close things
                    return;
                }
                switch (clientStatus) {
                    case OFFLINE:
                        decodeOffline();
                    case MAIN_MENU:
                        decodeMainMenu();
                    case IN_GAME:
                        decodeInGame();
                }

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                c.close();
                this.socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void decodeMainMenu() {

    }

    private void decodeInGame() {

    }

    public void kick() throws IOException, SQLException {
        this.out.writeChars("-1#01#You've been disconnected from server);");
        out.flush();
        socket.close();
        c.close();
        this.interrupt();
    }

    Player getPlayer() {
        return this.player;
    }

    private void decodeOffline() throws IOException {
        if (clientMessage.split("#")[0].equals("00")) {
            if (clientMessage.split("#")[1].equals("01")) {
                if (clientMessage.split("#")[2].equals("00")) {
                    //copy mode
                    
                    out.writeChars("00#03#"+DB.length()+"\n");
                    out.flush();
                } else {
                    //update mode

                }
            }else{
                FileInputStream fis = new FileInputStream(DB);
                BufferedInputStream bis = new BufferedInputStream(fis);
                byte[] byteArray = new byte[(int)DB.length()];
                bis.read(byteArray,0,byteArray.length);
                System.out.println("Sending file...\n");
                out.write(byteArray,0,byteArray.length);
                out.flush();
                System.out.println("Sent.\n");
            }
        }
    }

    private void copyDB() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
