package CommunikateWithClient;

import CommunicateWithData.CallingWebservice;
import CommunicateWithData.Chat;
import CommunicateWithData.ChatLog;
import CommunicateWithData.User;
import Server.AdministrateUser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import static javax.imageio.ImageIO.createImageInputStream;
import static javax.imageio.ImageIO.getImageReader;
import static javax.imageio.ImageIO.read;

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
                String jsonPicture = (String) jsonVersion.get("picture");

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
                        System.out.println("case group chat");
                        //Hvis brugeren vil chat, finder serveren KEY'en Chat og gemmer dens værdi
                        String ClientWroteToGroup = (String) jsonVersion.get("Chat");
                        String groupnumber = (String) jsonVersion.get("Group");

                        //laver json der skal sendes til den anden klient
                        jsonObject.put("function", "chat group");
                        jsonObject.put("data", ClientWroteToGroup);
                        jsonObject.put("username", owner);
                        jsonObject.put("groupnumber", groupnumber);

                        database.addChatLog(Integer.valueOf(groupnumber), owner, ClientWroteToGroup);

                        //JSON objektet bliver lavet om til en string og sendes til objektet der holder styr på alle klienter
                        message = jsonObject.toJSONString();
                        ArrayList<String> members = database.getGroupMembers(Integer.valueOf((String) jsonVersion.get("Group")));

                        System.out.println(members);

                        for (String groupMember: members) {
                            if (!(groupMember.equals(owner))) {
                                System.out.println("Sending to " + groupMember);
                                clients.writeToClient(groupMember, message);
                            }
                        }
                        break;

                    case "save profile picture":

                        System.out.println("her");
                        createImageInputStream(jsonPicture);
                        System.out.println(jsonPicture);





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

                    case "create user":

                        User user = new User(jsonUsername,jsonPassword);
                        database.createUser(user.getUsername(),user.getPassword());
                        System.out.println(user);
                        break;

                    case "Get Chatlog":
                        Long jsonCount = (Long) jsonVersion.get("Count");
                        int Count = jsonCount.intValue();

                        System.out.println("Count is: " + Count);

                        Long jsonGroupID = (Long) jsonVersion.get("GroupID");
                        int GroupID = jsonGroupID.intValue();

                        System.out.println("Group ID is: " + GroupID);


                        ArrayList<ChatLog> chatLogs;
                        if (Count == 2) {
                            chatLogs = database.getChatLogs(getChatId(Count, owner, jsonUsername));
                            //laver et jsonobjekt til klienten
                            jsonObject.put("function", "ChatLogs");
                            jsonObject.put("Username", jsonUsername);
                        } else {
                            System.out.println("Group ID: " + GroupID);
                            chatLogs = database.getChatLogs(GroupID);
                            jsonObject.put("function", "ChatLogs");
                            jsonObject.put("GroupID", GroupID);
                        }

                        ArrayList<String> logs = new ArrayList<>();

                        for (ChatLog chatlog : chatLogs) {
                            logs.add(chatlog.getLog());
                        }
                        jsonObject.put("Log", logs);


                        sendJson(jsonObject);

                        break;

                    case "VoiceChat":
                        //Finder IP fra clienten
                        String ip = (((InetSocketAddress) client.getRemoteSocketAddress()).getAddress()).toString().replace("/","");

                        //laver json der skal sendes til den anden klient
                        jsonObject.put("function", "VoiceChatRequest");
                        jsonObject.put("username", owner);
                        jsonObject.put("IP", ip);
                        jsonObject.put("PORT", jsonVersion.get("Count"));

                        System.out.println("IP address: " + (((InetSocketAddress) client.getRemoteSocketAddress()).getAddress()).toString().replace("/",""));

                        //JSON objektet bliver lavet om til en string og sendes til objektet der holder styr på alle klienter
                        message = jsonObject.toJSONString();
                        clients.writeToClient(jsonUsername, message);


                        break;

                    case "VoiceChatAccept":
                        //Finder IP fra clienten
                        ip = (((InetSocketAddress) client.getRemoteSocketAddress()).getAddress()).toString().replace("/","");

                        //laver json der skal sendes til den anden klient
                        jsonObject.put("function", "VoiceChatAccept");
                        jsonObject.put("username", owner);
                        jsonObject.put("IP", ip);
                        jsonObject.put("PORT", jsonVersion.get("Count"));

                        System.out.println("IP address: " + (((InetSocketAddress) client.getRemoteSocketAddress()).getAddress()).toString().replace("/",""));
                        System.out.println("Port is " + jsonVersion.get("Count"));
                        //JSON objektet bliver lavet om til en string og sendes til objektet der holder styr på alle klienter
                        message = jsonObject.toJSONString();
                        clients.writeToClient(jsonUsername, message);


                        break;

                    case "VoiceChatReject":
                        jsonObject.put("function", "VoiceChatReject");
                        jsonObject.put("username", owner);

                        message = jsonObject.toJSONString();
                        clients.writeToClient(jsonUsername, message);
                        break;

                    default:
                        System.out.println(jsonString);
                        System.out.println("no match");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            allClients.getallClientsInstance().removeClient(owner);
            System.out.println("Client disconnected");

        }

    }

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

