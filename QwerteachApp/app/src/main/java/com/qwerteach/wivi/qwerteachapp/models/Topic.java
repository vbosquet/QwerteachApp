package com.qwerteach.wivi.qwerteachapp.models;

/**
 * Created by wivi on 7/11/16.
 */

public class Topic {

    int topicId;
    String topicTitle;
    int topicGroupId;

    public Topic() {

    }

    public Topic(int topicId, String topicTitle, int topicGroupId) {
        this.topicId = topicId;
        this.topicGroupId = topicGroupId;
        this.topicTitle = topicTitle;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public int getTopicGroupId() {
        return topicGroupId;
    }

    public void setTopicGroupId(int topicGroupId) {
        this.topicGroupId = topicGroupId;
    }
}
