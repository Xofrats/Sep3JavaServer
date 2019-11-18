package CommunicateWithData;

import Server.Ven;


import java.util.ArrayList;

public class CallingDatabase {
    Database database = new Database();
    ArrayList<Ven> venner = new ArrayList<Ven>();

    public CallingDatabase() {

    }

   public ArrayList<String> GetVenner() {
       ArrayList<String> venner = database.getVenner();

       return venner;
   }
}
