package CommunicateWithData;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface IUser {

    @WebMethod
    User createUser(User userData);

/*    @WebMethod
    User validereBruger(String brugernavn, String password);*/

    @WebMethod
    void changePassword(int id, String password);

    @WebMethod
    User read(int id);

    @WebMethod
    void delete(int id);
}
