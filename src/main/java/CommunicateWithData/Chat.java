package CommunicateWithData;

public class Chat {
    private int chatID;
    private String chatName;

    public Chat() {
    }

    public Chat(int chatID, String chatName) {
        this.chatID = chatID;
        this.chatName = chatName;
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
