
import CommunicateWithData.CallingDatabase;
import Server.StartListening;

import java.net.Socket;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Runnable StartListening = new StartListening();

        new Thread(StartListening).start();
    }

}
