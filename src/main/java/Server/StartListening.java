package Server;

import CommunikateWithClient.CommunicateClient;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class StartListening implements Runnable {

    public void run() {
        try {
            //Laver en server socker på localhost port 7331
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            ServerSocket welcomeSocket = new ServerSocket(7331, 50, addr);


            while (true) {
                System.out.println("Waiting for a client");
                //Venter på en client
                Socket client = welcomeSocket.accept();
                System.out.println("Client connected");


                //Når en client kobler på serveren oprettes der en ny tråd og serveren går tilbage og venter på nye klienter
                Runnable InFromClient = new CommunicateClient(client);
                new Thread(InFromClient).start();


            }
        } catch (Exception e){
            System.out.println("Start listening Error");
            e.printStackTrace();
        }
    }
}
