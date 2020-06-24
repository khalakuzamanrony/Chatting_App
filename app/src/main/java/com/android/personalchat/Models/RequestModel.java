package com.android.personalchat.Models;

public  class RequestModel {
    String request_type,he,me;
    public RequestModel() {
    }

    public RequestModel(String request_type, String he, String me) {
        this.request_type = request_type;
        this.he = he;
        this.me = me;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getHe() {
        return he;
    }

    public void setHe(String he) {
        this.he = he;
    }

    public String getMe() {
        return me;
    }

    public void setMe(String me) {
        this.me = me;
    }
}
