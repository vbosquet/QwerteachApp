package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by wivi on 9/11/16.
 */

public class SmallAdPrice implements Serializable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("level_id")
    private Integer levelId;
    @SerializedName("price")
    private String price;
    @SerializedName("offer_id")
    private Integer smallAdId;

    public SmallAdPrice() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getSmallAdId() {
        return smallAdId;
    }

    public void setSmallAdId(Integer smallAdId) {
        this.smallAdId = smallAdId;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SmallAdPrice) {
            SmallAdPrice temp = (SmallAdPrice) obj;
            if(Objects.equals(this.levelId, temp.levelId) && Objects.equals(this.price, temp.price))
                return true;
        }
        return false;
    }
}
