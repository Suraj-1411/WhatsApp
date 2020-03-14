package com.example.whatsapp.ModelClasses;

public class Model {

    String proImage;
    String Name;
    String About;
    String UserID;

    public Model() {
    }

    public Model(String proImage, String name, String about,String userid) {
        this.proImage = proImage;
        Name = name;
        About = about;
        UserID=userid;
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

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }
    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
