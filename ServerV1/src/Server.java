
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

public class Server {

    private static Connection con;
    static final int SERVERPORT = 4444;
    private static TreeMap listOfPlayers;
    private static Socket socket = null;
    static final int DB_VERSION = 1;

    public static void main(String args[]) {

        try {
            ServerSocket serverSocket = null;
            con = DriverManager.getConnection("jdbc:derby://localhost:1527/GameDB");
            System.out.println("DB is rdy\n");
            serverSocket = new ServerSocket(SERVERPORT);
            System.out.println("Server is started...\n");

            while (true) {
                socket = serverSocket.accept();
                System.out.println("Client is connected\n");
                // login proccess...
                decodeOffline(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                con.close();
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void decodeOffline(Socket socket) throws IOException, SQLException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        String clientMessage = br.readLine();
        // 00 code is for login only and only!!!
        if (clientMessage.split("#")[0].equals("00")) {
            String username = clientMessage.split("#")[1];
            String pass = clientMessage.split("#")[2];
            ClientStatus cs = ClientStatus.OFFLINE;
            long id;
            //loging process...
            PreparedStatement ps = con.prepareStatement("SELECT * FROM PLAYERS WHERE USERNAME = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            // loging attemps!
            for (int i = 0; i < 5; i++) {
                if ((rs.getString("USERNAME") == null) || (rs.getString("PASS") != pass)) {
                    System.out.println("Wrong Username or Password\n");
                    out.writeChars("00#-1#00#Wrong Username or Password\n");
                    out.flush();
                    socket.close();
                } else {
                    //check if player is already online!!!
                    id = rs.getLong("ID");
                    ServerThread st;
                    Player player;
                    if (listOfPlayers.get(id) != null) {

                        st =  (ServerThread) listOfPlayers.get(id);
                        st.kick();
                        player = st.getPlayer();
                    }else{
                    player = new Player(username, cs, id);
                    
                    }
                    out.writeChars("00#01#"+DB_VERSION+"#Autentication successfull\n");
                    st = new ServerThread(socket, player);
                    st.start();
                    listOfPlayers.put(id, st);
                    out.flush();
                }
            }
        } else {
            // Ban process !... later Implementation
            out.writeChars("00#-1#01#Wrong Parameter\n);");
            out.flush();
            socket.close();
            System.out.println("Clinet is been kicked\n");
        }

    }
}
