import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Database {

    public ArrayList<Ven> getVenner() {
        ArrayList<Ven> venner = new ArrayList<Ven>();
        Connection c = null;
        Statement stmt = null;
        try {
            System.out.println("Making database connection");
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres", "1234");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            //her bliver tabellen hentet
            ResultSet rs = stmt.executeQuery( "SELECT * FROM \"Sep3\".Venner;" );
            //Dette loop laver et spild objekt for hver eneste linje i tabellen
            while ( rs.next() ) {
                String ID = rs.getString("ID");
                String username = rs.getString("username");
                venner.add(new Ven(ID, username));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venner;
    }
}
