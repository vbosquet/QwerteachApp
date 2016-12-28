package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;

/**
 * Created by wivi on 23/12/16.
 */

public class Message implements Serializable{
    private int messageId;
    private String body;
    private String subject;
    private int senderId;
    private int conversationId;
    private String creationDate;
    private boolean isMine;

    public Message(int messageId, String body, String subject, int senderId, int conversationId, String creationDate, boolean isMine) {
        this.messageId = messageId;
        this.body = body;
        this.subject = subject;
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.creationDate = creationDate;
        this.isMine = isMine;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }
}
