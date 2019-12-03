package CommunikateWithClient;

import CommunicateWithData.CallingWebservice;
import Server.AdministrateUser;
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

                //Objektet er et hashmap. En KEY er function. Værdien der tilhører function bliver gemt i en string
                String jsonString = (String) jsonVersion.get("Function");
                String jsonUsername = (String) jsonVersion.get("Username");
                String jsonPassword = (String) jsonVersion.get("Password");
                ArrayList<String> request = database.getfriendRequest("TEST");



                switch(jsonString)
                {
                    case "Chat":

                        //Hvis brugeren vil chat, finder serveren KEY'en Chat og gemmer dens værdi
                        String WhatClientWrote = (String) jsonVersion.get("Chat");
                        //laver json der skal sendes til den anden klient
                        jsonObject.put("function", "chat");
                        jsonObject.put("data", WhatClientWrote);
                        jsonObject.put("username", owner);

                        //JSON objektet bliver lavet om til en string og sendes til objektet der holder styr på alle klienter
                        message = jsonObject.toJSONString();
                       clients.writeToClient((String) jsonVersion.get("Username"), message);
                        break;

                    case "Add friend":

                        //bruger metode fra webservice
                        String check = database.checkUser("Mette", jsonUsername);

                        if (check.equals("Valid")) {
                            String getRequest = database.friendRequest("Mette", jsonUsername);

                            jsonObject.put("SendFriendRequest", getRequest);
                            jsonObject.put("function", "MyFriendRequest");

                            message = jsonObject.toJSONString();
                            b = message.getBytes();

                            //Byte arrayen bliver sendt til klienten
                            outToClient.write(b);
                        }
                        break;

                    case "friend request":

                        // Starter med at hente alle friend request
                        System.out.println(request);
                        jsonObject.put("FriendRequest", request);
                        jsonObject.put("function", "friendList");

                        message = jsonObject.toJSONString();
                        b = message.getBytes();

                        //Byte arrayen bliver sendt til klienten
                        outToClient.write(b);
                        break;

                    case "Accepted":

                        // Den bruger man har accepteret bliver registreret som ven
                        String friend = database.addFriend("TEST", jsonUsername);

                        // Hvis brugeren er registreret som ven, får man besked
                        if (friend.equals("Friend added")) {
                            //Listen bliver gemt i et JSONobejktet under KEY'en accepted
                            jsonObject.put("accepted", friend);

                            message = jsonObject.toJSONString();
                            b = message.getBytes();

                            //Byte arrayen bliver sendt til klienten
                            outToClient.write(b);


                            // Når brugeren acceptere en venneanmodning bliver der hentet en ny liste med venneanmodninger
                            jsonObject.put("FriendRequest", request);
                            jsonObject.put("function", "friendList");

                            message = jsonObject.toJSONString();
                            b = message.getBytes();

                            //Byte arrayen bliver sendt til klienten
                            outToClient.write(b);
                        }
                        break;

                    case "Delete friend":
                        //Den gemmer brugernavnene i en array
                        database.deleteFriend("TEST",jsonUsername);
                        break;

                    case "GetFriends":

                        //Den gemmer brugernavnene i en array
                        ArrayList<String> getfriends = database.getAllFriends("TEST");

                        //Listen bliver gemt i et JSONobejktet under KEY'en Data
                        jsonObject.put("data", getfriends);

                        //Byte arrayen bliver sendt til klienten
                        outToClient.write(b);
                        break;

                    case "Login":
                        AdministrateUser administrateUser = new AdministrateUser();
                        //checker om brugeren og kodeord er i databasen
                        if (administrateUser.logIn(jsonUsername, (String)jsonVersion.get("Password"))) {

                            //laver et jsonobjekt til klienten
                            jsonObject.put("function", "Login");
                            jsonObject.put("data", "Valid");
                            //Gemmer socket med tilhørende brugernavn
                            clients.addClient(jsonUsername, client);
                            //sætter trådens navn til den der loggede ind
                            owner = jsonUsername;
                            //Sender json til klienten
                            message = jsonObject.toJSONString();
                            b = message.getBytes();
                            outToClient.write(b);
                        }

                        break;

                        case "Create user":

                        //bruger metode fra webservice
                        String create = database.createUser(jsonUsername, jsonPassword);
                        break;

                        default:
                        System.out.println("no match");
                        }

                        }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Client disconnected");

        }
        }
    }

