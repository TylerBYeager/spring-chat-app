package com.example.chat;

import com.example.chat.Message;
import com.example.chat.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;

    // 🧠 Thread-safe set to track active users
    private final Set<String> connectedUsers = Collections.synchronizedSet(new HashSet<>());

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        logger.info("A user has connected.");
        // We don't know the username yet — this happens in ChatController
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            connectedUsers.remove(username); // Remove user
            logger.info("User Disconnected: " + username);

            // Broadcast leave message
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Message leaveMsg = new Message("System", username + " has left the chat", time, MessageType.LEAVE);
            messagingTemplate.convertAndSend("/topic/messages", leaveMsg);

            // Broadcast updated user list
            messagingTemplate.convertAndSend("/topic/users", connectedUsers);
        } else {
            logger.info("User Disconnected: Unknown");
        }
    }

    // This will be called manually from ChatController
    public void addUser(String username) {
        if (username != null && !username.isBlank()) {
            connectedUsers.add(username);
            messagingTemplate.convertAndSend("/topic/users", connectedUsers);
        }
    }
}
