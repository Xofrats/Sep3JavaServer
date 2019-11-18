package CommunicateWithData;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;


@Path("/users")
@WebService
public class UserDAO implements IUser {

    private CallDatabase callDatabase = CallDatabase.CallDB();

    public UserDAO() {}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<User> allUsers() {
        return callDatabase.getUser();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User createUser(User userData) {
        ArrayList<User> userList = callDatabase.getUser();
        int id = userList.size();
        User user = new User(id, userData.getBrugernavn(),userData.getPassword());
        callDatabase.postUser(user);
        return user;
    }

/*    @GET
    @Path("/validate/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User validereBruger(@PathParam("id") String brugernavn, String password) {
        for(User user: userList) {
            if (user.getPassword().equals(password) && user.getBrugernavn().equals(brugernavn))
                return user;
        }
        return null;
    }*/


    @PUT
    @Path("/changePW/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void changePassword(@PathParam("id") int id, String password) {
        callDatabase.putPassword(id,password);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User read(@PathParam("id") int id)
    {
        return callDatabase.getOneUser(id);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") int id) {
        callDatabase.deleteUser(id);
    }
}
