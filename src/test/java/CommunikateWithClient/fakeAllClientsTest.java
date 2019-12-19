package CommunikateWithClient;

import org.junit.Test;

import java.net.Socket;
import java.util.HashMap;

import static org.junit.Assert.*;

public class fakeAllClientsTest {
    private HashMap<String, Socket> clientsList = new HashMap<>();
    private HashMap<String, String> resultList = new HashMap<>();

    private fakeAllClients allClients = new fakeAllClients();

    @Test
    public void writeToClient() {

        allClients.addClient("Test", new Socket());

       allClients.writeToClient("Test", "Message");

       resultList = allClients.getResult();
       assertEquals(allClients.getResult().get("Test"), "Message");
    }

    @Test
    public void addClient() {
allClients.addClient("Test", new Socket());
clientsList = allClients.getAllClients();

assertTrue(clientsList.containsKey("Test"));
    }

    @Test
    public void removeClient() {
        allClients.addClient("Test", new Socket());
        allClients.removeClient("Test");
        clientsList = allClients.getAllClients();

        assertFalse(clientsList.containsKey("Test"));
    }
}