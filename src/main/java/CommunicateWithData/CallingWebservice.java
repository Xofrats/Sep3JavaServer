package CommunicateWithData;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

public class CallingWebservice {

    private  ClientConfig config = new ClientConfig();
    private Client client;
    private WebTarget target;

    public CallingWebservice() {
        client = ClientBuilder.newClient(config);
        target = client.target("http://localhost:8080/WebServices");
    }

    public ArrayList<String> getAllFriends(String owner) {
        GenericType<ArrayList<Friend>> userArrayListType = new GenericType<ArrayList<Friend>>() {
        };

        ArrayList<Friend> allUsers = target.path("friends").path(owner).request().accept(MediaType.APPLICATION_JSON).get(userArrayListType);

        ArrayList<String> allNames = new ArrayList<>();
        for (Friend friendList : allUsers) {
            allNames.add(friendList.getUsername());
        }
        return allNames;
    }

    public String friendRequest(String username) {
        // Går ind i web service bruger en GET metode
        //
        return target.path("friends").path("friendRequest").path(username).request().accept(MediaType.APPLICATION_JSON).get(String.class);
    }

    public void addFriend(String owner, String username) {
        // Går ind i web service og bruger POST metoden
        // En ven bliver tilføjet med det username som er modtaget af owner
        target.path("friends").path(owner).request(MediaType.APPLICATION_JSON).post(Entity.json(username));
    }

    public void deleteFriend(String owner, String username) {
        //
        target.path("friends").path(owner).path(username).request().delete();
    }

    public User getUser(String username) {

        User user = target.path("users").path(username).request().accept(MediaType.APPLICATION_JSON).get(User.class);

        return user;
    }
}
