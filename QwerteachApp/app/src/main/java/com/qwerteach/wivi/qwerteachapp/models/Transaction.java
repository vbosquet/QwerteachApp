package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;

/**
 * Created by wivi on 9/12/16.
 */

public class Transaction implements Serializable{

    private String transactionId;
    private String date;
    private String type;
    private String authorId;
    private String creditedUserId;
    private String creditedAmount;
    private String debitedAmount;
    private String fees;
    private String authorName;
    private String creditedUserName;

    public Transaction(String transactionId, String date, String type, String authorId, String creditedUserId, String creditedAmount, String debitedAmount, String fees) {
        this.transactionId = transactionId;
        this.date = date;
        this.type = type;
        this.authorId = authorId;
        this.creditedUserId = creditedUserId;
        this.creditedAmount = creditedAmount;
        this.debitedAmount = debitedAmount;
        this.fees = fees;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getCreditedUserId() {
        return creditedUserId;
    }

    public void setCreditedUserId(String creditedUserId) {
        this.creditedUserId = creditedUserId;
    }

    public String getCreditedAmount() {
        return creditedAmount;
    }

    public void setCreditedAmount(String creditedAmount) {
        this.creditedAmount = creditedAmount;
    }

    public String getDebitedAmount() {
        return debitedAmount;
    }

    public void setDebitedAmount(String debitedAmount) {
        this.debitedAmount = debitedAmount;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCreditedUserName() {
        return creditedUserName;
    }

    public void setCreditedUserName(String creditedUserName) {
        this.creditedUserName = creditedUserName;
    }
}
