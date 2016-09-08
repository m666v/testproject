
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class Server{

    static final int SERVERPORT = 4444;

    public static void main(String args[]) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(SERVERPORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server is started...\n");
        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("Client is connected\n");
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new Thread for a client
            new ServerThread(socket).run();
            System.out.println("New Thread is been called.\n");
        }
    }
}
