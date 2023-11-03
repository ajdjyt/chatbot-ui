package com.ajdjyt.springbootwebsocket.controller;

import com.ajdjyt.springbootwebsocket.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

@Controller
public class ChatController {

    String apiURL = "http://127.0.0.1:8000/chat/";

    @MessageMapping("/chat.register")
    @SendTo("/topic/public")
    public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        try{
        String prompt = "{\"prompt\": \""+chatMessage.getContent()+"\"}";
        URL url = new URL(apiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            out.writeBytes(prompt);
            out.flush();
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray responseArray = jsonResponse.getJSONArray("response");
                String mainText = responseArray.getString(0);
                chatMessage.setContent(mainText.substring(mainText.indexOf(">",mainText.indexOf(">",mainText.indexOf(">")+1)+1)+10,mainText.length()));
                System.out.println(mainText);
                
            }
        } else {
            chatMessage.setContent("HTTP Request Failed with error code: " + responseCode);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessage;
    }
}