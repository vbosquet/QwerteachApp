package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;

/**
 * Created by wivi on 9/11/16.
 */

public class SmallAdPrice implements Serializable{
    int id;
    int smallAdId;
    int levelId;
    double price;

    public SmallAdPrice() {

    }

    public SmallAdPrice(int id, int smallAdId, int levelId, double price) {
        this.id = id;
        this.smallAdId = smallAdId;
        this.levelId = levelId;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSmallAdId() {
        return smallAdId;
    }

    public void setSmallAdId(int smallAdId) {
        this.smallAdId = smallAdId;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
