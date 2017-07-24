package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;
import com.qwerteach.wivi.qwerteachapp.common.Common;

import java.io.Serializable;

/**
 * Created by wivi on 23/12/16.
 */

public class Message implements Serializable {

    @SerializedName("id")
    private Integer messageId;
    @SerializedName("body")
    private String body;
    @SerializedName("subject")
    private String subject;
    @SerializedName("sender_id")
    private Integer senderId;
    @SerializedName("conversation_id")
    private Integer conversationId;
    @SerializedName("created_at")
    private String creationDate;
    @SerializedName("recipient")
    private Integer recipient;

    private Boolean isMine;
    private String avatar;

    public Message(String subject, String body, Integer recipient) {
        this.subject = subject;
        this.body = body;
        this.recipient = recipient;

    }

    public Message(int messageId, String body, String subject, int senderId, int conversationId, String creationDate, boolean isMine) {
        this.messageId = messageId;
        this.body = body;
        this.subject = subject;
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.creationDate = creationDate;
        this.isMine = isMine;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
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

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getMine() {
        return isMine;
    }

    public void setMine(Boolean mine) {
        isMine = mine;
    }

    public Integer getRecipient() {
        return recipient;
    }

    public void setRecipient(Integer recipient) {
        this.recipient = recipient;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = Common.IP_ADDRESS + avatar;
    }
}
