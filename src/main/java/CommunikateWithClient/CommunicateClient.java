package CommunikateWithClient;

import CommunicateWithData.CallingWebservice;
import CommunicateWithData.User;
import Server.AdministrateUser;
import Server.allClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class CommunicateClient implements Runnable {
    private Socket client;
    private String owner;
    private allClients clients = allClients.getallClientsInstance();

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

                System.out.println("Function is: " + jsonString);

                String jsonUsername = (String) jsonVersion.get("Username");

                switch(jsonString)
                {
                    case "Chat":

                        //Hvis brugeren vil chat, finder serveren KEY'en Chat og gemmer dens værdi
                        String WhatClientWrote = (String) jsonVersion.get("Chat");
                        String whoToSendTo = (String) jsonVersion.get("Username");
                        //indtil videre laver den et echo
                        jsonObject.put("function", "chat");
                        jsonObject.put("data", WhatClientWrote);
                        jsonObject.put("username", owner);

                        //JSON objektet bliver lavet om til en string og derefter en byte array
                        message = jsonObject.toJSONString();
                       clients.writeToClient(whoToSendTo, message);
                        break;

                    case "Add friend":

                        //bruger metode fra webservice
                        String venner = database.friendRequest(jsonUsername);

                        //Listen bliver gemt i et JSONobejktet under KEY'en Data

                        jsonObject.put("name", venner);

                        //Byte arrayen bliver sendt til klienten
                        outToClient.write(b);
                        break;

                    case "Accepted":

                        //Den gemmer brugernavnene i en array
                        database.addFriend("TEST", jsonUsername);
                        break;

                    case "Delete friend":
                        //Den gemmer brugernavnene i en array
                        database.deleteFriend("TEST",jsonUsername);
                        break;
                    case "GetFriends":

                        //Den gemmer brugernavnene i en array
                        ArrayList<String> getfriends = database.getAllFriends("TEST");

                        //Listen bliver gemt i et JSONobejktet under KEY'en Data
                        jsonObject.put("function", "alleVenner");
                        jsonObject.put("data", getfriends);

                        message = jsonObject.toJSONString();
                        b = message.getBytes();

                        //Byte arrayen bliver sendt til klienten
                        outToClient.write(b);
                        break;

                    case "Login":
                        AdministrateUser administrateUser = new AdministrateUser();
                        System.out.println("Username: " + jsonUsername);
                        System.out.println("password: " + jsonVersion.get("Password"));
                        System.out.println(administrateUser.logIn(jsonUsername, (String)jsonVersion.get("Password")));
                        if (administrateUser.logIn(jsonUsername, (String)jsonVersion.get("Password"))) {
                            System.out.println("valid user");
                            //Listen bliver gemt i et JSONobejktet under KEY'en Data
                            jsonObject.put("function", "Login");
                            jsonObject.put("data", "Valid");

                            clients.addClient(jsonUsername, client);
                            owner = jsonUsername;

                            message = jsonObject.toJSONString();
                            b = message.getBytes();

                            //Byte arrayen bliver sendt til klienten
                            outToClient.write(b);
                        }

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
