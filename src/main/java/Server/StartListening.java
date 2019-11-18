package Server;

import CommunikateWithClient.CommunikateClient;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class StartListening implements Runnable {




    public void run() {
        try {
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            ServerSocket welcomeSocket = new ServerSocket(7331, 50, addr);


            while (true) {
                System.out.println("Waiting for a client");

                Socket client = welcomeSocket.accept();
                System.out.println("Client connected");




                Runnable CommunikateClient = new CommunikateClient(client);
                new Thread(CommunikateClient).start();


            }
        } catch (Exception e){
            System.out.println("Start listening Error");
            System.out.println(e.getStackTrace());
        }
    }
}
