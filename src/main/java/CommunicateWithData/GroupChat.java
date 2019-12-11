package CommunicateWithData;

public class GroupChat {

    int groupID;
    String username;
    Boolean admin;

    public GroupChat() {
    }

    public GroupChat(int groupID, String username, Boolean admin) {
        this.groupID = groupID;
        this.username = username;
        this.admin = admin;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
