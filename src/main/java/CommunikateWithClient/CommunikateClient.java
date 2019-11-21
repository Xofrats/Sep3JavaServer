package CommunikateWithClient;

import CommunicateWithData.CallingWebservice;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class CommunikateClient implements Runnable {
    private Socket client;

    public CommunikateClient(Socket client) {
        this.client = client;
    }

    public void run() {

        try {
            //henter clientens streams
            OutputStream outToClient = client.getOutputStream();
            InputStream inFromClient = client.getInputStream();

            //Laver et json objekt der kan sendes over stream
            JSONObject testingJson = new JSONObject();

            //Laver en buffer der kan omskrive json til data stream
            BufferedReader r = new BufferedReader(new InputStreamReader(inFromClient, "US-ASCII"));

            //En test af forbindelsen
            testingJson.put("name", "Welcome");
            String welcome = testingJson.toJSONString();
            byte[] hey = welcome.getBytes();
            outToClient.write(hey);

            while (true) {
                //Så længe tråden kører venter serveren på inputs fra klienten
                String clientWant = r.readLine();

                //JSONparser kan oversætte JSON fra streamen til java
                JSONParser parser = new JSONParser();

                //Laver et JSON objekt ud fra streamen
                JSONObject jsonVersion = (JSONObject) parser.parse(clientWant);
                //Objektet er et hashmap. En af KEY er function. Værdien der tilhører function bliver gemt i en string
                String jsonString = (String) jsonVersion.get("Function");
                //Printer værdien ud
                System.out.println(jsonString);

                //Værdien kommer fra knapperne. Server checker hvilken funktion den skal bruge her
                if (jsonString.equals("Chat")){

                    //Hvis brugeren vil chat, finder serveren KEY'en Chat og gemmer dens værdi
                    String WhatClientWrote = (String) jsonVersion.get("Chat");
                    //indtil videre laver den et echo
                    testingJson.put("name", WhatClientWrote);
                    //JSON objektet bliver lavet om til en string og derefter en byte array
                    String message = testingJson.toJSONString();
                    byte[] b = message.getBytes();
                    //Byte arrayen bliver sendt til klienten
                    outToClient.write(b);

                   // CommunikateWithClient.CommunicateToAll all = new CommunikateWithClient.CommunicateToAll();
                   // all.talkToAll(message);
                }

                if (jsonString.equals("Add friend")) {

                    //Serveren laver et objekt der kan snakke med webservicen
                    CallingWebservice database = new CallingWebservice();

                    //bruger metode fra webservice
                    String venner = database.friendRequest();

                    //Listen bliver gemt i et JSONobejktet under KEY'en Data
                    testingJson.put("name", venner);

                    //JSON bliver lavet om til en string og der efter en byte array
                    String message = testingJson.toJSONString();
                    byte[] b = message.getBytes();

                    //Byte arrayen bliver sendt til klienten
                    outToClient.write(b);

                }
                if (jsonString.equals("Accepted")) {
                    CallingWebservice database = new CallingWebservice();

                    //Den gemmer brugernavnene i en array
                    database.addFriend();
                }
                if (jsonString.equals("Delete friend")) {
                    //Hvis klienten vil hente sin venne liste, laver serveren et objekt der kan snakke med webservicen
                    CallingWebservice database = new CallingWebservice();
                    //Den gemmer brugernavnene i en array
                    database.deleteFriend();

                } else {
                    //Hvis klienten vil hente sin venne liste, laver serveren et objekt der kan snakke med webservicen
                    CallingWebservice database = new CallingWebservice();

                    //Den gemmer brugernavnene i en array
                    ArrayList<String> venner = database.getAllFriends();

                    //Listen bliver gemt i et JSONobejktet under KEY'en Data
                    testingJson.put("data", venner);

                    //JSON bliver lavet om til en string og der efter en byte array
                    String message = testingJson.toJSONString();
                    byte[] b = message.getBytes();

                    //Byte arrayen bliver sendt til klienten
                    outToClient.write(b);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Client disconnected");
        }
    }
}
