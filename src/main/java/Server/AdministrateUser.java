package Server;

import CommunicateWithData.CallingWebservice;
import CommunicateWithData.User;

public class AdministrateUser {

    CallingWebservice callingWebservice = new CallingWebservice();

    public boolean logIn(String username, String password){
        User user = callingWebservice.getUser(username);

        if (user.getPassword().equals(password)) {
            return true;
        } else {
            return false;
        }
    }
}
