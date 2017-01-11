package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wivi on 4/11/16.
 */

public class SmallAd implements Serializable {

    private String title;
    private int advertId;
    private int topicId;
    private int topicGroupId;
    private String description;
    private int userId;
    private ArrayList<SmallAdPrice> smallAdPrices;

    public SmallAd() {
    }

    public SmallAd(String title, int advertId, int topicId, int topicGroupId, String description) {
        this.title = title;
        this.advertId = advertId;
        this.topicId = topicId;
        this.topicGroupId = topicGroupId;
        this.description = description;
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

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getTopicGroupId() {
        return topicGroupId;
    }

    public void setTopicGroupId(int topicGroupId) {
        this.topicGroupId = topicGroupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ArrayList<SmallAdPrice> getSmallAdPrices() {
        return smallAdPrices;
    }

    public void setSmallAdPrices(ArrayList<SmallAdPrice> smallAdPrices) {
        this.smallAdPrices = smallAdPrices;
    }
}
