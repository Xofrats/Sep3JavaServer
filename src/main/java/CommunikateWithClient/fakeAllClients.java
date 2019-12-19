package CommunikateWithClient;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class fakeAllClients implements IWriteToClients {

    private HashMap<String, Socket> allClients = new HashMap<>();
    private HashMap<String, String> result = new HashMap<>();

    public fakeAllClients() {

    }

    public HashMap<String, Socket> getAllClients() {
        return allClients;
    }

    public HashMap<String, String> getResult() {
        return result;
    }

    public void writeToClient(String username, String message) {
        if (allClients.containsKey(username)) {
                result.put(username, message);

        } else {
            System.out.println("No client with that name");
        }
    }

    @Override
    public void addClient(String username, Socket socket) {
allClients.put(username,socket);
    }

    @Override
    public void removeClient(String username) {
allClients.remove(username);
    }
}
