package com.qwerteach.wivi.qwerteachapp.models;

/**
 * Created by wivi on 7/11/16.
 */

public class TopicGroup {

    int topicGroupId;
    String topicGroupTitle;
    String levelCode;

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
