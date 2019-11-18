package CommunikateWithClient;

import CommunicateWithData.CallingDatabase;

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
            OutputStream outToClient = client.getOutputStream();
            InputStream inFromClient = client.getInputStream();

            JSONObject testingJson = new JSONObject();
            BufferedReader r = new BufferedReader(new InputStreamReader(inFromClient, "US-ASCII"));

            testingJson.put("name", "Welcome");
            String welcome = testingJson.toJSONString();
            byte[] hey = welcome.getBytes();
            outToClient.write(hey);

            while (true) {
                String clientWant = r.readLine();
                JSONParser parser = new JSONParser();
                JSONObject jsonVersion = (JSONObject) parser.parse(clientWant);

                String jsonString = (String) jsonVersion.get("Function");
                System.out.println(jsonString);

                if (jsonString.equals("Chat")){
                    String WhatClientWrote = (String) jsonVersion.get("Chat");
                    testingJson.put("name", WhatClientWrote);
                    String message = testingJson.toJSONString();
                    byte[] b = message.getBytes();
                    outToClient.write(b);

                   // CommunikateWithClient.CommunicateToAll all = new CommunikateWithClient.CommunicateToAll();
                   // all.talkToAll(message);

                } else {
                    System.out.println("Gik i else");
                    CallingWebservice database = new CallingWebservice();
                    System.out.println("made webservice");

                    ArrayList<String> venner = database.getAllUsers();
                    System.out.println("Venner listen: " + venner);
                    testingJson.put("data", venner);
                    String message = testingJson.toJSONString();
                    byte[] b = message.getBytes();
                    outToClient.write(b);
                }

            }

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Client disconnected");
        }
    }
}
