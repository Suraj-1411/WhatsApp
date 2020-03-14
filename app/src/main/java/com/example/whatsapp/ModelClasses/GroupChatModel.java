package com.example.whatsapp.ModelClasses;

public class GroupChatModel {

    String message;
    String type;
    String name;

    public GroupChatModel() {
    }

    public GroupChatModel(String message, String type, String name) {
        this.message = message;
        this.type = type;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
