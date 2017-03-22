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
    private String levelName;
    private boolean isChecked;

    public Level() {

    }

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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Level) {
            Level temp = (Level) obj;
            if(Objects.equals(this.levelName, temp.levelName) && this.levelId == temp.levelId)
                return true;
        }
        return false;
    }
}
