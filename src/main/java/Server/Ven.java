package Server;

public class Ven {
    String ID;
    String Username;

    public Ven(String ID, String username) {
        this.ID = ID;
        Username = username;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    @Override
    public String toString() {
        return "Server.Ven{" +
                "ID='" + ID + '\'' +
                ", Username='" + Username + '\'' +
                '}';
    }
}
