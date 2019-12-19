package CommunikateWithClient;

import java.net.Socket;

public interface IWriteToClients {
public void writeToClient(String username, String message);
public void addClient(String username, Socket socket);
public void removeClient(String username);
}
