package com.android.personalchat.Models;

public class Friends_Model {
    private String date, receiver, sender;

    public Friends_Model() {
    }

    public Friends_Model(String date, String receiver, String sender) {
        this.date = date;
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
