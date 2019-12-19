package CommunikateWithClient;

import org.junit.Test;

import java.net.Socket;
import java.util.HashMap;

import static org.junit.Assert.*;

public class allClientsTest {
    private HashMap<String, Socket> clientsList = new HashMap<>();
    allClients allClientsClass = allClients.getallClientsInstance();

    @Test
    public void addClient() {
        allClientsClass.addClient("Test", new Socket());
        clientsList = allClientsClass.getAllClients();

        assertTrue(clientsList.containsKey("Test"));
    }

    @Test
    public void removeClient() {
        allClientsClass.addClient("Test", new Socket());
        allClientsClass.removeClient("Test");
        clientsList = allClientsClass.getAllClients();

        assertFalse(clientsList.containsKey("Test"));
    }
}