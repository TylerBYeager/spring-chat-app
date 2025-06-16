package com.example.chat;

public class Message {
        private String from;
        private String content;
        private String time;
        private MessageType type;

        public Message() {}
        public Message(String from, String content, String time, MessageType type) {
            this.from = from;
            this.content = content;
            this.time = time;
            this.type = type;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() { return time; }

        public void setTime(String time) { this.time = time; }

        public MessageType getType() { return type; }

        public void setType(MessageType type) {
            this.type = type;
        }
}
