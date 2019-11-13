import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.Socket;
import java.util.ArrayList;

public class CommunicateToAll {

    private ArrayList<Socket> allClient = new ArrayList<Socket>();

    public void addClient(Socket client){
        allClient.add(client);
    }

    public void talkToAll(String clientWant){
        try {
            System.out.println("writing to all");
            byte[] b = clientWant.getBytes();
            for (Socket client: allClient
                 ) {
                client.getOutputStream().write(b);
            }

        } catch (Exception e){
            System.out.println("Failed to send to all");
        }
    }


}
