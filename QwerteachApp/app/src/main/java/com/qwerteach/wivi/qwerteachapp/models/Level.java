package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by wivi on 7/11/16.
 */

public class Level implements Serializable {

    @SerializedName("id")
    private int levelId;
    @SerializedName("be")
    private String beLevelName;
    @SerializedName("fr")
    private String frLevelName;
    private boolean isChecked;
    private boolean needBeLevel;

    public Level() {

    }

    public  Level(int levelId, String levelName) {
        this.levelId = levelId;
        this.beLevelName = levelName;
        isChecked = false;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getBeLevelName() {
        return beLevelName;
    }

    public void setBeLevelName(String beLevelName) {
        this.beLevelName = beLevelName;
    }

    public String getFrLevelName() {
        return frLevelName;
    }

    public void setFrLevelName(String frLevelName) {
        this.frLevelName = frLevelName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isNeedBeLevel() {
        return needBeLevel;
    }

    public void setNeedBeLevel(boolean needBeLevel) {
        this.needBeLevel = needBeLevel;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Level) {
            Level temp = (Level) obj;
            if(Objects.equals(this.beLevelName, temp.beLevelName) && this.levelId == temp.levelId)
                return true;
        }
        return false;
    }
}
