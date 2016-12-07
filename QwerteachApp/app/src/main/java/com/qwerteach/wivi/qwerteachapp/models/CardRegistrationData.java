package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;

/**
 * Created by wivi on 7/12/16.
 */

public class CardRegistrationData implements Serializable {

    private String accessKey;
    private String preRegistrationData;
    private String cardRegistrationURL;
    private String cardPreregistrationId;

    public CardRegistrationData(String accessKey, String preRegistrationData, String cardRegistrationURL, String cardPreregistrationId) {
        this.accessKey = accessKey;
        this.preRegistrationData = preRegistrationData;
        this.cardRegistrationURL = cardRegistrationURL;
        this.cardPreregistrationId = cardPreregistrationId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getPreRegistrationData() {
        return preRegistrationData;
    }

    public void setPreRegistrationData(String preRegistrationData) {
        this.preRegistrationData = preRegistrationData;
    }

    public String getCardRegistrationURL() {
        return cardRegistrationURL;
    }

    public void setCardRegistrationURL(String cardRegistrationURL) {
        this.cardRegistrationURL = cardRegistrationURL;
    }

    public String getCardPreregistrationId() {
        return cardPreregistrationId;
    }

    public void setCardPreregistrationId(String cardPreregistrationId) {
        this.cardPreregistrationId = cardPreregistrationId;
    }
}
