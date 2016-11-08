package com.qwerteach.wivi.qwerteachapp.models;

/**
 * Created by wivi on 4/11/16.
 */

public class SmallAd {

    private String title;
    private int advertId;

    public SmallAd(String title, int advertId) {
        this.title = title;
        this.advertId = advertId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAdvertId() {
        return advertId;
    }

    public void setAdvertId(int advertId) {
        this.advertId = advertId;
    }
}
