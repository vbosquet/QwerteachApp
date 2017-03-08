package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wivi on 4/11/16.
 */

public class SmallAd implements Serializable {

    @SerializedName("id")
    private Integer advertId;
    @SerializedName("topic_id")
    private Integer topicId;
    @SerializedName("topic_group_id")
    private Integer topicGroupId;
    @SerializedName("description")
    private String description;
    @SerializedName("user_id")
    private Integer userId;
    @SerializedName("other_name")
    private String otherName;
    @SerializedName("offer_prices_attributes")
    private ArrayList<SmallAdPrice> smallAdPrices;

    private String title;

    public SmallAd() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<SmallAdPrice> getSmallAdPrices() {
        return smallAdPrices;
    }

    public void setSmallAdPrices(ArrayList<SmallAdPrice> smallAdPrices) {
        this.smallAdPrices = smallAdPrices;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getTopicGroupId() {
        return topicGroupId;
    }

    public void setTopicGroupId(Integer topicGroupId) {
        this.topicGroupId = topicGroupId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
