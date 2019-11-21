package CommunicateWithData;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

public class CallingWebservice {

    private  ClientConfig config = new ClientConfig();
    private Client client;
    private WebTarget target;

    public CallingWebservice() {
        client = ClientBuilder.newClient(config);
        target = client.target("http://localhost:8080/WebServices");
    }

    public ArrayList<String> getAllFriends() {
        GenericType<ArrayList<Friend>> userArrayListType = new GenericType<ArrayList<Friend>>() {
        };

        ArrayList<Friend> allUsers = target.path("friends").request().accept(MediaType.APPLICATION_JSON).get(userArrayListType);

        ArrayList<String> allNames = new ArrayList<>();
        for (Friend friendList : allUsers) {
            System.out.println(friendList.toString());
            allNames.add(friendList.getUsername());
        }
        return allNames;
    }

}
