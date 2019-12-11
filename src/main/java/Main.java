import CommunicateWithData.CallingWebservice;
import CommunicateWithData.ChatLog;
import Server.StartListening;

public class Main {

    public static void main(String[] args) {

        Runnable StartListening = new StartListening();
        new Thread(StartListening).start();

        CallingWebservice data = new CallingWebservice();

       //System.out.println("new chat: " + data.getChatId(2, "CT", "morten"));
        /*System.out.println(data.getAllFriends("Sitch"));

        System.out.println("chatlog" + data.addChatLog(1, "morten", "Hello"));*/
    }

}
