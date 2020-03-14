package com.example.whatsapp.ModelClasses;

public class Message {
    String message,type;/*,time;*/

    public Message() {
    }

    public Message(String message, String type, String time) {
        this.message = message;
        this.type = type;
        /*this.time = time;*/
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

    /*public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }*/
}
