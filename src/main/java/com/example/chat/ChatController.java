package com.example.chat;

import org.springframework.cglib.core.Local;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    @MessageMapping("/chat") // from client: /app/chat
    @SendTo("/topic/messages") // broadcasts to: /topic/messages
    public Message send(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        // Save username to WebSocket session
        if (headerAccessor.getSessionAttributes().get("username") == null && message.getFrom() != null) {
            headerAccessor.getSessionAttributes().put("username", message.getFrom());
        }

        message.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        message.setType(MessageType.CHAT);
        return message;
    }

    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public String typing(@Payload String from) {
        return from + " is typing...";
    }

}
