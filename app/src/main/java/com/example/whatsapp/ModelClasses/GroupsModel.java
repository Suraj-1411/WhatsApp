package com.example.whatsapp.ModelClasses;

public class GroupsModel {

    String groupIcon;
    String groupname;
    String membersize;

    public GroupsModel() {
    }

    public GroupsModel(String groupIcon, String groupname, String membersize) {
        this.groupIcon = groupIcon;
        this.groupname = groupname;
        this.membersize = membersize;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getMembersize() {
        return membersize;
    }

    public void setMembersize(String membersize) {
        this.membersize = membersize;
    }
}
