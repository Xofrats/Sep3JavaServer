package CommunikateWithClient;

import CommunicateWithData.CallingWebservice;
import CommunicateWithData.Chat;
import CommunicateWithData.ChatLog;
import CommunicateWithData.GroupChat;
import Server.AdministrateUser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class CommunicateClient implements Runnable {
    private Socket client;
    private String owner;
    private allClients clients = allClients.getallClientsInstance();
    private  boolean running = true;

    public CommunicateClient(Socket client) {
        this.client = client;
    }
    //Laver et json objekt der kan sendes over stream
    JSONObject jsonObject = new JSONObject();

    //JSONparser kan oversætte JSON fra streamen til java
    JSONParser parser = new JSONParser();

    //Serveren laver et objekt der kan snakke med webservicen
    CallingWebservice database = new CallingWebservice();

    byte[] b = new byte[1024];
    String message;

    public void run() {

        try {
            //henter clientens streams
            OutputStream outToClient = client.getOutputStream();
            InputStream inFromClient = client.getInputStream();
            //Laver en buffer der kan omskrive json til data stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inFromClient, "UTF-8"));

            while (running) {
                //Så længe tråden kører venter serveren på inputs fra klienten
                String clientWant = reader.readLine();
                //Laver et JSON objekt ud fra streamen
                JSONObject jsonVersion = (JSONObject) parser.parse(clientWant);

                //Objektet er et hashmap. En KEY er function. Værdien der tilhører function bliver gemt i en string.
                //Function bliver hentet da den bliver brugt i switchen
                String jsonString = (String) jsonVersion.get("Function");
                //Username bliver hentet da den bliver brugt i de fleste cases
                String jsonUsername = (String) jsonVersion.get("Username");
                String jsonPassword = (String) jsonVersion.get("Password");

                switch (jsonString) {
                    case "Chat":

                        //Hvis brugeren vil chat, finder serveren KEY'en Chat og gemmer dens værdi
                        String WhatClientWrote = (String) jsonVersion.get("Chat");

                        //laver json der skal sendes til den anden klient
                        jsonObject.put("function", "chat");
                        jsonObject.put("data", WhatClientWrote);
                        jsonObject.put("username", owner);

                        database.addChatLog(getChatId(2, owner, jsonUsername), owner, WhatClientWrote);
                        //JSON objektet bliver lavet om til en string og sendes til objektet der holder styr på alle klienter
                        message = jsonObject.toJSONString();
                        clients.writeToClient((String) jsonVersion.get("Username"), message);
                        break;

                    case "Group chat":

                        //Hvis brugeren vil chat, finder serveren KEY'en Chat og gemmer dens værdi
                        String ClientWroteToGroup = (String) jsonVersion.get("Chat");

                        ArrayList<String> group = database.getGroupChat(owner);

                        //laver json der skal sendes til den anden klient
                        jsonObject.put("function", "chat group");
                        jsonObject.put("data", ClientWroteToGroup);
                        jsonObject.put("username", "2");

                        //JSON objektet bliver lavet om til en string og sendes til objektet der holder styr på alle klienter
                        message = jsonObject.toJSONString();
                        ArrayList<String> members = database.getGroupMembers(Integer.valueOf((String) jsonVersion.get("Group")));

                        System.out.println(members);

                        for (String groupMembers: members) {
                            System.out.println(groupMembers);
                            clients.writeToClient(groupMembers, message);
                        }
                        break;

                    case "Send file":
                        //Hvis brugeren vil chat, finder serveren KEY'en Chat og gemmer dens værdi
                        String file = (String) jsonVersion.get("Chat");
                        String fileName = (String) jsonVersion.get("fileName");

                        //laver json der skal sendes til den anden klient
                        jsonObject.put("function", "Send file");
                        jsonObject.put("File", file);
                        jsonObject.put("NameOfFile", fileName);
                        jsonObject.put("username", owner);

                        //JSON objektet bliver lavet om til en string og sendes til objektet der holder styr på alle klienter
                        message = jsonObject.toJSONString();
                        clients.writeToClient((String) jsonVersion.get("Username"), message);
                        break;

                    case "Add friend":

                        if (jsonUsername != null && !jsonUsername.isEmpty()) {
                            //bruger metode fra webservice
                            String check = database.checkUser(jsonUsername);


                            if (check.equals("Valid")) {
                                String getRequest = database.friendRequest(owner, jsonUsername);

                                jsonObject.put("SendFriendRequest", getRequest);
                                jsonObject.put("function", "MyFriendRequest");

                                sendJson(jsonObject);
                            }
                        } else {
                            jsonObject.put("SendFriendRequest", "Write an username");
                            jsonObject.put("function", "MyFriendRequest");

                            sendJson(jsonObject);
                        }
                        break;

                    case "friend request":

                        ArrayList<String> request = database.getfriendRequest(owner);

                        // Starter med at hente alle friend request
                        jsonObject.put("FriendRequest", request);
                        jsonObject.put("function", "friendList");

                        sendJson(jsonObject);
                        break;

                    case "Accepted":

                        if (jsonUsername != null && !jsonUsername.isEmpty()) {
                            // Den bruger man har accepteret bliver registreret som ven
                            String friend = database.addFriend(owner, jsonUsername);

                            // Hvis brugeren er registreret som ven, får man besked
                            if (friend.equals("Friend added")) {
                                //Listen bliver gemt i et JSONobejktet under KEY'en accepted
                                jsonObject.put("accepted", friend);
                                jsonObject.put("function", "newFriend");

                                sendJson(jsonObject);
                            }
                        } else {
                            //Listen bliver gemt i et JSONobejktet under KEY'en accepted
                            jsonObject.put("accepted", "The box is empty");
                            jsonObject.put("function", "newFriend");

                            sendJson(jsonObject);
                        }
                        break;

                    case "Rejected":

                        if (jsonUsername != null && !jsonUsername.isEmpty()) {
                            //Den gemmer brugernavnene i en array
                            String reject = database.rejectUser(owner, jsonUsername);

                            jsonObject.put("RejectUser", reject);
                            jsonObject.put("function", "UserRejected");

                            sendJson(jsonObject);
                        } else {
                            //Listen bliver gemt i et JSONobejktet under KEY'en accepted
                            jsonObject.put("accepted", "The box is empty");
                            jsonObject.put("function", "newFriend");

                            sendJson(jsonObject);
                        }
                        break;

                    case "Delete friend":

                        if (jsonUsername != null && !jsonUsername.isEmpty()) {
                            //Den gemmer brugernavnene i en array
                            String delete = database.deleteFriend(owner, jsonUsername);

                            jsonObject.put("DeleteUser", delete);
                            jsonObject.put("function", "UserDeleted");

                            sendJson(jsonObject);
                        } else {
                            jsonObject.put("DeleteUser", "Write an username");
                            jsonObject.put("function", "UserDeleted");

                            sendJson(jsonObject);
                        }
                        break;

                    case "GetFriends":
                        System.out.println("Getting friends for " + owner);

                        //Den gemmer brugernavnene i en array
                        ArrayList<String> getfriends = database.getAllFriends(owner);

                        System.out.println("friends: " + getfriends);

                        //Listen bliver gemt i et JSONobejktet under KEY'en Data
                        jsonObject.put("function", "allFriends");
                        jsonObject.put("data", getfriends);

                        sendJson(jsonObject);

                        break;

                    case "Get groups":
                        System.out.println("Getting groups from " + owner);

                        //Den gemmer brugernavnene i en array
                        ArrayList<String> getGroups = database.getGroupChat(owner);

                        System.out.println("groups: " + getGroups);

                        //Listen bliver gemt i et JSONobejktet under KEY'en Data
                        jsonObject.put("function", "allGroups");
                        jsonObject.put("data", getGroups);

                        message = jsonObject.toJSONString();
                        b = message.getBytes();

                        //Byte arrayen bliver sendt til klienten
                        outToClient.write(b);

                        break;

                    case "Login":

                        if (jsonUsername != null && !jsonUsername.isEmpty() && jsonPassword != null && !jsonPassword.isEmpty()) {
                            System.out.println("Logging in");
                            AdministrateUser administrateUser = new AdministrateUser();
                            //checker om brugeren og kodeord er i databasen
                            if (administrateUser.logIn(jsonUsername, (String) jsonVersion.get("Password"))) {

                                //laver et jsonobjekt til klienten
                                jsonObject.put("function", "Login");
                                jsonObject.put("data", "Valid");
                                //Gemmer socket med tilhørende brugernavn
                                clients.addClient(jsonUsername, client);
                                //sætter trådens navn til den der loggede ind
                                owner = jsonUsername;
                                //Sender json til klienten
                                sendJson(jsonObject);
                            }
                        } else {
                            jsonObject.put("data", "Fill out the field(s)");
                            jsonObject.put("function", "Login");

                            sendJson(jsonObject);
                        }

                        break;

                    case "Create user":

                        //bruger metode fra webservice
                        //String create = database.createUser(jsonUsername, jsonPassword);
                        break;

                    case "Get Chatlog":
                        Long jsonCount = (Long) jsonVersion.get("Count");
                        int Count = jsonCount.intValue();

                        ArrayList<ChatLog> chatLogs = database.getChatLogs(getChatId(Count, owner, jsonUsername));

                        ArrayList<String> logs = new ArrayList<>();

                        for (ChatLog chatlog : chatLogs) {
                            logs.add(chatlog.getLog());
                        }
                        //laver et jsonobjekt til klienten
                        jsonObject.put("function", "ChatLogs");
                        jsonObject.put("Log", logs);
                        jsonObject.put("Username", jsonUsername);
                        System.out.println(jsonUsername);

                        sendJson(jsonObject);

                        break;

                    default:
                        System.out.println(jsonString);
                        System.out.println("no match");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Client disconnected");

        }
        } /*catch (ParseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    public void sendJson(JSONObject jsonObject){
        try{
            OutputStream outToClient = client.getOutputStream();

            message = jsonObject.toJSONString();
            b = message.getBytes();
            outToClient.write(b);

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public int getChatId(int count, String owner, String username) {
        return Integer.valueOf(database.getChatId(count, owner, username));

    }
}

