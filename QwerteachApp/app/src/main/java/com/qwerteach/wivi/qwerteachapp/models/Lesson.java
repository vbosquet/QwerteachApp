package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;

/**
 * Created by wivi on 7/12/16.
 */

public class Lesson implements Serializable {

    private int lessonId;
    private int teacherId;
    private int topicId;
    private int topicGroupId;
    private int levelId;
    private String price;
    private String timeStart;
    private String teacherFirstName;
    private String teacherLastName;
    private String topicTitle;
    private String duration;
    private boolean expired;
    private boolean canceled;

    public Lesson(int lessonId, int teacherId, int topicId, int topicGroupId, int levelId, String price) {
        this.lessonId = lessonId;
        this.teacherId = teacherId;
        this.topicId = topicId;
        this.topicGroupId = topicGroupId;
        this.levelId = levelId;
        this.price = price;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getTopicGroupId() {
        return topicGroupId;
    }

    public void setTopicGroupId(int topicGroupId) {
        this.topicGroupId = topicGroupId;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTeacherFirstName() {
        return teacherFirstName;
    }

    public void setTeacherFirstName(String teacherFirstName) {
        this.teacherFirstName = teacherFirstName;
    }

    public String getTeacherLastName() {
        return teacherLastName;
    }

    public void setTeacherLastName(String teacherLastName) {
        this.teacherLastName = teacherLastName;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
