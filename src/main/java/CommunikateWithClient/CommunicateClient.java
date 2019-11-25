package CommunikateWithClient;

import CommunicateWithData.CallingWebservice;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class CommunicateClient implements Runnable {
    private Socket client;

    public CommunicateClient(Socket client) {
        this.client = client;
    }

    public void run() {

        try {
            //henter clientens streams
            OutputStream outToClient = client.getOutputStream();
            InputStream inFromClient = client.getInputStream();

            //Laver et json objekt der kan sendes over stream
            JSONObject jsonObject = new JSONObject();

            //JSONparser kan oversætte JSON fra streamen til java
            JSONParser parser = new JSONParser();

            //Laver en buffer der kan omskrive json til data stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inFromClient, "US-ASCII"));

            //Serveren laver et objekt der kan snakke med webservicen
            CallingWebservice database = new CallingWebservice();

            byte[] b = new byte[1024];
            String message;



            while (true) {
                //Så længe tråden kører venter serveren på inputs fra klienten
                String clientWant = reader.readLine();
                //Laver et JSON objekt ud fra streamen
                JSONObject jsonVersion = (JSONObject) parser.parse(clientWant);

                //Objektet er et hashmap. En af KEY er function. Værdien der tilhører function bliver gemt i en string
                String jsonString = (String) jsonVersion.get("Function");

                switch(jsonString)
                {
                    case "Chat":

                        //Hvis brugeren vil chat, finder serveren KEY'en Chat og gemmer dens værdi
                        String WhatClientWrote = (String) jsonVersion.get("Chat");
                        //indtil videre laver den et echo
                        jsonObject.put("function", "chat");
                        jsonObject.put("name", WhatClientWrote);
                        //JSON objektet bliver lavet om til en string og derefter en byte array
                        message = jsonObject.toJSONString();
                        b = message.getBytes();
                        //Byte arrayen bliver sendt til klienten
                        outToClient.write(b);
                        break;

                    case "Add friend":
                        //bruger metode fra webservice
                        String venner = database.friendRequest();

                        //Listen bliver gemt i et JSONobejktet under KEY'en Data
                        jsonObject.put("name", venner);

                        //JSON bliver lavet om til en string og der efter en byte array
                        message = jsonObject.toJSONString();
                        b = message.getBytes();

                        //Byte arrayen bliver sendt til klienten
                        outToClient.write(b);
                        break;

                    case "Accepted":
                        //Den gemmer brugernavnene i en array
                        database.addFriend();
                        break;

                    case "Delete friend":
                        //Den gemmer brugernavnene i en array
                        database.deleteFriend();
                        break;
                    case "GetFriends":
                        System.out.println("getting friends");
                        //Den gemmer brugernavnene i en array
                        ArrayList<String> alleVenner = database.getAllFriends();

                        //Listen bliver gemt i et JSONobejktet under KEY'en Data
                        jsonObject.put("function", "alleVenner");
                        jsonObject.put("data", alleVenner);

                        //JSON bliver lavet om til en string og der efter en byte array
                        message = jsonObject.toJSONString();
                        b = message.getBytes();

                        //Byte arrayen bliver sendt til klienten
                        outToClient.write(b);

                        break;
                /* tom case
                 case "keyword":

                        break;*/
                    default:
                        System.out.println("no match");
                }
            }

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Client disconnected");
        }
    }
}
