package com.qwerteach.wivi.qwerteachapp.models;

/**
 * Created by wivi on 7/11/16.
 */

public class Level {

    private int levelId;
    private String levelName;
    private boolean isChecked;
    private double price;

    public  Level(int levelId, String levelName) {
        this.levelId = levelId;
        this.levelName = levelName;
        isChecked = false;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
