
import CommunicateWithData.CallingDatabase;
import Server.StartListening;

import java.net.Socket;
import java.util.List;

public class Main {
    public static List<Socket> clients;
    public static void main(String[] args) {
        CallingDatabase callingDatabase = new CallingDatabase();

        Runnable StartListening = new StartListening();

        new Thread(StartListening).start();



    }

}
