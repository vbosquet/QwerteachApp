package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wivi on 22/12/16.
 */

public class Conversation implements Serializable{
    private int conversationId;
    private String subject;
    private String conversationCreationDate;
    private String conversationUpdatingDate;
    private User user;
    private ArrayList<Message> messages;

    public Conversation(int conversationId, String subject, String conversationCreationDate, String conversationUpdatingDate) {
        this.conversationId = conversationId;
        this.subject = subject;
        this.conversationCreationDate = conversationCreationDate;
        this.conversationUpdatingDate = conversationUpdatingDate;
        messages = new ArrayList<>();
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
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

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
