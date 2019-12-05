import CommunicateWithData.CallingWebservice;
import CommunicateWithData.ChatLog;
import Server.StartListening;

public class Main {

    public static void main(String[] args) {

        Runnable StartListening = new StartListening();
        new Thread(StartListening).start();

        CallingWebservice data = new CallingWebservice();

        System.out.println("Chat ID: " + data.getChatId(2,"morten", "Sitch"));

        int chatId = Integer.valueOf(data.getChatId(2, "morten", "Sitch"));

        for (ChatLog log:
             data.getChatLogs(chatId)) {
            System.out.println(log.toString());
        }
    }

}
