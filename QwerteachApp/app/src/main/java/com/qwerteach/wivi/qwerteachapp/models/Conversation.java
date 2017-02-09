package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wivi on 22/12/16.
 */

public class Conversation implements Serializable{

    @SerializedName("id")
    private Integer conversationId;
    @SerializedName("subject")
    private String subject;
    @SerializedName("created_at")
    private String conversationCreationDate;
    @SerializedName("updated_at")
    private String conversationUpdatingDate;
    private User user;
    private List<Message> messages;

    public Conversation() {
        messages = new ArrayList<>();
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getConversationCreationDate() {
        return conversationCreationDate;
    }

    public void setConversationCreationDate(String conversationCreationDate) {
        this.conversationCreationDate = conversationCreationDate;
    }

    public String getConversationUpdatingDate() {
        return conversationUpdatingDate;
    }

    public void setConversationUpdatingDate(String conversationUpdatingDate) {
        this.conversationUpdatingDate = conversationUpdatingDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessageToConverstaion(Message message) {
        messages.add(message);
    }
}
