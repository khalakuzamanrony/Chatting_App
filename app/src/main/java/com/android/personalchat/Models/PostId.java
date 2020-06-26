package com.android.personalchat.Models;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

public  class PostId {
    @Exclude
    public  String Postid;
    public <T extends PostId> T withid (@NonNull final String id){
        this.Postid=id;
        return (T) this;
    }


}
