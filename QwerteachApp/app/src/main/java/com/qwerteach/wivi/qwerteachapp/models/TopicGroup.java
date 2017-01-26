package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wivi on 7/11/16.
 */

public class TopicGroup {

    @SerializedName("id")
    private int topicGroupId;
    @SerializedName("title")
    private String topicGroupTitle;
    @SerializedName("level_code")
    private String levelCode;

    public TopicGroup() {

    }

    public TopicGroup(int topicGroupId, String topicGroupTitle, String levelCode) {
        this.topicGroupId = topicGroupId;
        this.topicGroupTitle= topicGroupTitle;
        this.levelCode = levelCode;
    }

    public int getTopicGroupId() {
        return topicGroupId;
    }

    public void setTopicGroupId(int topicGroupId) {
        this.topicGroupId = topicGroupId;
    }

    public String getTopicGroupTitle() {
        return topicGroupTitle;
    }

    public void setTopicGroupTitle(String topicGroupTitle) {
        this.topicGroupTitle = topicGroupTitle;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }
}
