import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CommunikateClient implements Runnable {
    private Socket client;

    public CommunikateClient(Socket client) {
        this.client = client;
    }

    public void run() {
        byte[] data = new byte[1024];

        try {
            OutputStream outToClient = client.getOutputStream();
            InputStream InFromServer = client.getInputStream();

            String message = "Hello World";
            byte[] b = message.getBytes();
            outToClient.write(b);

            while (true){
              int test = InFromServer.read();
                System.out.println("Got: " + (char)test);

            }

        } catch (Exception e){
            System.out.println("Error");
        }
    }
}
