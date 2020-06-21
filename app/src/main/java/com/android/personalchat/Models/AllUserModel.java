package com.android.personalchat.Models;

public class AllUserModel {
    String name, status, profile_image, thumb_image, id,online_status;
    //long online_status;

    public AllUserModel() {
    }

    public AllUserModel(String name, String status, String profile_image, String thumb_image, String id, String online_status) {
        this.name = name;
        this.status = status;
        this.profile_image = profile_image;
        this.thumb_image = thumb_image;
        this.id = id;
        this.online_status = online_status;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
