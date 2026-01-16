package com.example.coligify.Model;

public class ChatMessageModel {
    public String chatId;
    public String message;
    public boolean isUser;

    public ChatMessageModel(String chatId, String message, boolean isUser) {
        this.chatId = chatId;
        this.message = message;
        this.isUser = isUser;
    }
}
