package CommunikateWithClient;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class allClients {

    private HashMap<String, Socket> allClients = new HashMap<>();

    private static allClients clientsinst;

    private allClients(){}

    public static allClients getallClientsInstance(){
        if(clientsinst == null){
            clientsinst = new allClients();
        }
        return clientsinst;
    }

    public void addClient(String username, Socket socket){
        allClients.put(username, socket);
    }

    public void writeToClient(String username, String message){
        try {
            allClients.get(username).getOutputStream().write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
