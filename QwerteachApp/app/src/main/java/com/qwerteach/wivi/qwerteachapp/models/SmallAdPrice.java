package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;

/**
 * Created by wivi on 9/11/16.
 */

public class SmallAdPrice implements Serializable{
    private int id;
    private int levelId;
    private double price;

    public SmallAdPrice(int id, int levelId, double price) {
        this.id = id;
        this.levelId = levelId;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
