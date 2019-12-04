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

    public String checkUser(String owner, String username) {
        // Går ind i web service og ser om det indtastede username findes i databasen
        return target.path("friends").path("checkUser").path(owner).path(username).request().accept(MediaType.APPLICATION_JSON).get(String.class);
    }

    public String friendRequest(String owner, String username) {
        // Går ind i web service og sender en venneanmodning
        return target.path("friends").path("friendRequest").path(owner).path(username).request().accept(MediaType.APPLICATION_JSON).get(String.class);
    }

    public ArrayList<String> getfriendRequest(String owner) {
        GenericType<ArrayList<Friend>> friendRequest = new GenericType<ArrayList<Friend>>() {
        };

        ArrayList<Friend> allFriends = target.path("friends").path("getFriendRequest").path(owner).request().accept(MediaType.APPLICATION_JSON).get(friendRequest);

        ArrayList<String> allRequestNames = new ArrayList<>();
        for (Friend friendList : allFriends) {
            allRequestNames.add(friendList.getUsername());
        }
        return allRequestNames;
    }

    public String addFriend(String owner, String username) {

        System.out.println("Inde i callingWebservice");
        // En ven bliver tilføjet med det username som er modtaget af owner
        return target.path("friends").path(owner).request(MediaType.APPLICATION_JSON).post(Entity.json(username)).readEntity(String.class);
    }

    public String rejectUser(String owner, String username) {
        //
        System.out.println("Inde i CW");
        return target.path("friends").path("rejectUser").path(owner).path(username).request().delete().readEntity(String.class);
    }

    public String deleteFriend(String owner, String username) {
        //
        return target.path("friends").path(owner).path(username).request().delete().readEntity(String.class);
    }

    public User getUser(String username) {

        User user = target.path("users").path(username).request().accept(MediaType.APPLICATION_JSON).get(User.class);

        return user;
    }

    /*public String createUser(String username, String password) {

        String request = "username=" + username + "&password=" + password;


        return target.path("/users").path(username).request(MediaType.APPLICATION_JSON).post(Entity.json(request)).toString();
    }*/


}
