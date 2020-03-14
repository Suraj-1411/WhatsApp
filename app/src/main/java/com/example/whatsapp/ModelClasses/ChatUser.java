package com.example.whatsapp.ModelClasses;

public class ChatUser {

    String proImage,Name,lastseen,UserID;

    public ChatUser() {
    }

    public ChatUser(String proImage, String name, String lastseen, String userID) {
        this.proImage = proImage;
        Name = name;
        this.lastseen = lastseen;
        UserID = userID;
    }

    public String getProImage() {
        return proImage;
    }

    public void setProImage(String proImage) {
        this.proImage = proImage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastseen() {
        return lastseen;
    }

    public void setLastseen(String lastseen) {
        this.lastseen = lastseen;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
