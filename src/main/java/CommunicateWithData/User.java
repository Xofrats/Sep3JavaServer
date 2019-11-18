package CommunicateWithData;

import java.util.Objects;

public class User {
    private int id;
    private String brugernavn;
    private String password;

    public User() {}

    public User(int id, String brugernavn, String password) {
        this.id = id;
        this.brugernavn = brugernavn;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrugernavn() {
        return brugernavn;
    }

    public void setBrugernavn(String brugernavn) {
        this.brugernavn = brugernavn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(brugernavn, user.brugernavn) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brugernavn, password);
    }

    @Override
    public String toString() {
        return  "id: " + id +
                "   brugernavn: " + brugernavn +
                "   password: " + password +
                "\n";
    }
}
