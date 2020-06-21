package com.android.personalchat.Models;


public class MessageModel {
    String message, seen, type, from;
    long time;

    public MessageModel() {
    }


    public MessageModel(String message, String seen, String type, String from, long time) {
        this.message = message;
        this.seen = seen;
        this.type = type;
        this.from = from;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
