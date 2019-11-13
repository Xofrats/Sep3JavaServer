
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

                } else {
                    CallingDatabase database = new CallingDatabase();

                    ArrayList<String> venner = database.GetVenner();
                    testingJson.put("data", venner);
                    String message = testingJson.toJSONString();
                    byte[] b = message.getBytes();
                    outToClient.write(b);
                }

            }

        } catch (Exception e){
            System.out.println("Client disconnected");
        }
    }
}
