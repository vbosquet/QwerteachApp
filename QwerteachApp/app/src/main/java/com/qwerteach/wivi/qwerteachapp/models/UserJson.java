package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;

/**
 * Created by wivi on 20/01/17.
 */

public class UserJson {

    @SerializedName("user")
    private User user = new User();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
