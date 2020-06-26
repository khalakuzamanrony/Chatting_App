package com.android.personalchat.Models;

public class Blog2Model {
    String id,image,text,time;

    public Blog2Model() {
    }

    public Blog2Model(String id, String image, String text, String time) {
        this.id = id;
        this.image = image;
        this.text = text;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
