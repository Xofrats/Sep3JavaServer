import CommunicateWithData.CallingWebservice;
import Server.StartListening;

public class Main {

    public static void main(String[] args) {

        Runnable StartListening = new StartListening();
        new Thread(StartListening).start();

        CallingWebservice callingWebservice = new CallingWebservice();
        System.out.println(callingWebservice.getUser("Sitch"));
    }

}
