package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;

/**
 * Created by wivi on 1/12/16.
 */

public class UserCreditCard implements Serializable {
    private String alias;
    private String cardId;

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
}
