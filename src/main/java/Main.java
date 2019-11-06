import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        CallingDatabase callingDatabase = new CallingDatabase();
        try {
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            ServerSocket welcomeSocket = new ServerSocket(7331, 50, addr);
            Socket socket = welcomeSocket.accept();
            System.out.println("Client connected");

            OutputStream outToClient = socket.getOutputStream();
            InputStream InFromServer = socket.getInputStream();

            String message = "Hello World";
            byte[] b = message.getBytes();
            outToClient.write(b);

while(true) {
    String s = Integer.toString(InFromServer.read());
    b = s.getBytes();

    outToClient.write(b);
}



        } catch (Exception e){
            System.out.println("Error");
        }
    }
}
