package com.qwerteach.wivi.qwerteachapp.models;

/**
 * Created by wivi on 9/12/16.
 */

public class Transaction {

    private int transactionId;
    private String date;
    private String type;
    private int authorId;
    private int creditedUserId;
    private String creditedAmount;
    private String debitedAmount;
    private String fees;
    private String authorName;
    private String creditedUserName;

    public Transaction(int transactionId, String date, String type, int authorId, int creditedUserId, String creditedAmount, String debitedAmount, String fees) {
        this.transactionId = transactionId;
        this.date = date;
        this.type = type;
        this.authorId = authorId;
        this.creditedUserId = creditedUserId;
        this.creditedAmount = creditedAmount;
        this.debitedAmount = debitedAmount;
        this.fees = fees;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
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

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getCreditedUserId() {
        return creditedUserId;
    }

    public void setCreditedUserId(int creditedUserId) {
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
