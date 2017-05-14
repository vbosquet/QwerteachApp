package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wivi on 9/12/16.
 */

public class Transaction implements Serializable{

    @SerializedName("id")
    private String transactionId;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("type")
    private String type;
    @SerializedName("author_id")
    private String authorId;
    @SerializedName("credited_user_id")
    private String creditedUserId;
    @SerializedName("debited_funds")
    private DebitedFund debitedFund;
    @SerializedName("credited_funds")
    private CreditedFund creditedFund;
    @SerializedName("fees")
    private Fee fee;
    @SerializedName("status")
    private String status;
    @SerializedName("result_message")
    private String resultMessage;

    private String title;

    public Transaction() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
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

    public String getCreditedUserId() {
        return creditedUserId;
    }

    public DebitedFund getDebitedFund() {
        return debitedFund;
    }

    public CreditedFund getCreditedFund() {
        return creditedFund;
    }

    public Fee getFee() {
        return fee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class DebitedFund implements Serializable {

        @SerializedName("currency")
        private String currency;
        @SerializedName("amount")
        private Integer amount;

        DebitedFund() {

        }

        public String getCurrency() {
            return currency;
        }
        public Integer getAmount() {
            return amount;
        }
    }

    public static class CreditedFund implements Serializable {

        @SerializedName("currency")
        private String currency;
        @SerializedName("amount")
        private Integer amount;

        CreditedFund() {

        }

        public String getCurrency() {
            return currency;
        }
        public Integer getAmount() {
            return amount;
        }

    }

    public static class Fee implements Serializable {

        @SerializedName("currency")
        private String currency;
        @SerializedName("amount")
        private Integer amount;

        Fee() {

        }

        public String getCurrency() {
            return currency;
        }
        public Integer getAmount() {
            return amount;
        }

    }
}
