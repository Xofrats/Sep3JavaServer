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

    public void removeClient(String username){
        allClients.remove(username);
    }

    public void writeToClient(String username, String message) {
        if (allClients.containsKey(username)) {
            System.out.println("Got user");
            try {
                allClients.get(username).getOutputStream().write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No client with that name");
        }
    }
}
