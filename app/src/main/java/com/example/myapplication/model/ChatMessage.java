package com.example.myapplication.model;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private String avatarUrl;
    private int ai_avatar;
    private int color;   // 0: not set, 1: red, 2: green

    public ChatMessage(String message, boolean isUser, String avatarUrl, int ai_avatar, int color) {
        this.message = message;
        this.isUser = isUser;
        this.avatarUrl = avatarUrl;
        this.ai_avatar = ai_avatar;
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getAiImage() {
        return "ai_" + ai_avatar;
    }

    public int getColor() {
        return color;
    }
}
