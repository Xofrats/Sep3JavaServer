import java.util.ArrayList;

public class CallingDatabase {
    Database database = new Database();
    ArrayList<Ven> venner = new ArrayList<Ven>();

    public CallingDatabase() {

    }

   public void WriteVenner() {
       ArrayList<Ven> venner = database.getVenner();
       for (Ven ven : venner
       ) {
           System.out.println(ven.toString());
       }
   }
}
