import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Socket> clients;
    public static void main(String[] args) {
        CallingDatabase callingDatabase = new CallingDatabase();

        Runnable StartListening = new StartListening();

        new Thread(StartListening).start();
    }

}
