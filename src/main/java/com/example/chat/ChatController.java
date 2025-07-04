package com.example.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketEventListener eventListener; // ✅ Inject listener

    public ChatController(SimpMessagingTemplate messagingTemplate, WebSocketEventListener eventListener) {
        this.messagingTemplate = messagingTemplate;
        this.eventListener = eventListener;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        message.setTime(time);

        // First time the user sends a message, capture and broadcast JOIN
        if (headerAccessor.getSessionAttributes().get("username") == null && message.getFrom() != null) {
            headerAccessor.getSessionAttributes().put("username", message.getFrom());

            // ✅ Track new user for user list updates
            eventListener.addUser(message.getFrom());

            // Broadcast JOIN message
            Message joinMessage = new Message(
                    "System",
                    message.getFrom() + " has joined the chat",
                    time,
                    MessageType.JOIN
            );
            messagingTemplate.convertAndSend("/topic/messages", joinMessage);
        }

        message.setType(MessageType.CHAT);
        return message;
    }

    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public String typing(@Payload String from, SimpMessageHeaderAccessor headerAccessor) {
        Object sessionUsername = headerAccessor.getSessionAttributes().get("username");

        if (sessionUsername != null) {
            return sessionUsername + " is typing...";
        }

        return "";
    }
}
