
import java.beans.Statement;
import java.io.BufferedReader;
import java.io.DataOutputStream;
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

    //constructor
    public ServerThread(Socket socket, Player player) {
        this.socket = socket;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            c = DriverManager.getConnection("jdbc:derby://localhost:1527/PlayersDB");
            inputStream = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(inputStream));
            out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                //to jump out of while block and finish the thread!
                clientMessage = br.readLine();
                if(this.isInterrupted()||(clientMessage == null)) {
                    //finally will close things
                    return;
                }
                switch (clientStatus) {
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
            try{
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

   
    public void kick() throws IOException, SQLException{
        this.out.writeBytes("-1#01#You've been disconnected from server);");
        out.flush();
        socket.close();
        c.close();
        this.interrupt();
    }

    Player getPlayer() {
        return this.player;
    }

}
