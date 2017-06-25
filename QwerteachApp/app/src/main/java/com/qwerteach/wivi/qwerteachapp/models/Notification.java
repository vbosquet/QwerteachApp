package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wivi on 18/06/17.
 */

public class Notification implements Serializable {

    private static final String BASE_URL = "http://192.168.1.21:3000";

    @SerializedName("id")
    Integer notification_id;
    @SerializedName("subject")
    String subject;
    @SerializedName("sender_id")
    Integer sender_id;
    @SerializedName("created_at")
    String created_at;
    @SerializedName("notified_object_type")
    String notification_type;

    String avatar;

    public Notification(){

    }

    public Integer getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(Integer notification_id) {
        this.notification_id = notification_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getSender_id() {
        return sender_id;
    }

    public void setSender_id(Integer sender_id) {
        this.sender_id = sender_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = BASE_URL + avatar;
    }

    public String getDate() {
        String dateFormated = (String) android.text.format.DateFormat.format("dd/MM/yyyy", changeStringToDate(this.getCreated_at()));
        return  dateFormated;

    }

    public String getTime() {
        String time = (String) android.text.format.DateFormat.format("HH:mm", changeStringToDate(this.getCreated_at()));
        return time;
    }

    private Date changeStringToDate(String string) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }
}
