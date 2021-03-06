package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wivi on 1/12/16.
 */

public class UserCreditCard implements Serializable {

    @SerializedName("alias")
    private String alias;
    @SerializedName("id")
    private String cardId;
    @SerializedName("expiration_date")
    private String expirationDate;
    @SerializedName("card_provider")
    private String cardProvider;
    @SerializedName("currency")
    private String currency;
    @SerializedName("validity")
    private String validity;

    public UserCreditCard(String alias, String cardId) {
        this.alias = alias;
        this.cardId = cardId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardProvider() {
        return cardProvider;
    }

    public void setCardProvider(String cardProvider) {
        this.cardProvider = cardProvider;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
