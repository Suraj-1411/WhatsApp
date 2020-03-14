package com.example.whatsapp.ModelClasses;

public class ChatModel {
    String proImage,Name,About;

    public ChatModel() {
    }

    public ChatModel(String proImage, String name, String about) {
        this.proImage = proImage;
        Name = name;
        About = about;
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
}
