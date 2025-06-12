package com.example.chat;

import org.springframework.cglib.core.Local;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    @MessageMapping("/chat") //from client: /app/chat
    @SendTo("/topic/messages") // broadcasts to:/topic/messages
    public OutputMessage send(Message message) {
        return new OutputMessage(
                message.getFrom(),
                message.getContent(),
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
    }

}
