package com.qwerteach.wivi.qwerteachapp.models;

/**
 * Created by wivi on 19/01/17.
 */

public class JsonResponse {

    private String avatar;
    private String message;
    private String success;
    private User user;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
