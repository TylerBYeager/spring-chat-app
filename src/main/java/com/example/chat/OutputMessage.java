package com.example.chat;

import java.time.LocalTime;

public class OutputMessage {
    private String from;
    private String content;
    private String time;

    public OutputMessage() {}
    public OutputMessage(String from, String content, String time) {
        this.from = from;
        this.content = content;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }


}
