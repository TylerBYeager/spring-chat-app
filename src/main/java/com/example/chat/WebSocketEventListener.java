package com.example.chat;

import com.example.chat.Message;
import com.example.chat.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        logger.info("A user has connected.");
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Message message = new Message("System", "A new user has joined the chat", time, MessageType.JOIN);
        messagingTemplate.convertAndSend("topic/messages", message);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Message message;

        if (username != null) {
            logger.info("User Disconnected: " + username);
            message = new Message("System", username + " has left the chat", time, MessageType.LEAVE);
        } else {
            logger.info("User Disconnected: Unknown");
            message = new Message("System", "A user has left the chat", time, MessageType.LEAVE);
        }

        messagingTemplate.convertAndSend("/topic/messages", message);
    }


}
