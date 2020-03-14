package com.example.whatsapp.ModelClasses;

public class CallsModel {

    String type;
    String userid;
    String time;


    public CallsModel(String type, String userid,String time) {
        this.type = type;
        this.userid = userid;
        this.time=time;
    }

    public CallsModel() {
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getUserid() {
        return userid;
    }
}
