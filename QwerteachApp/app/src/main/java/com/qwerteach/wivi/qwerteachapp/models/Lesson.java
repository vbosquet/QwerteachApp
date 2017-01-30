package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wivi on 7/12/16.
 */

public class Lesson implements Serializable {

    private Integer lessonId;
    @SerializedName("student_id")
    private Integer studentId;
    @SerializedName("teacher_id")
    private Integer teacherId;
    @SerializedName("topic_id")
    private Integer topicId;
    private Integer topicGroupId;
    @SerializedName("level_id")
    private Integer levelId;

    @SerializedName("time_start")
    private String timeStart;
    private String status;
    private String price;
    private String userFirstName;
    private String userLastName;
    private String topicTitle;
    private String topicGroupTitle;
    private String level;
    private String duration;
    private String paymentStatus;
    @SerializedName("hours")
    private String hours;
    @SerializedName("minutes")
    private String minutes;

    private Boolean reviewNeeded;

    public Lesson(){

    }

    public Lesson(int lessonId, int studentId, int teacherId, int topicId, int topicGroupId,
                  int levelId, String status, String price, String timeStart) {
        this.lessonId = lessonId;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.topicId = topicId;
        this.topicGroupId = topicGroupId;
        this.levelId = levelId;
        this.status = status;
        this.price = price;
        this.timeStart = timeStart;
    }

    public Integer getLessonId() {
        return lessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getTopicGroupId() {
        return topicGroupId;
    }

    public void setTopicGroupId(Integer topicGroupId) {
        this.topicGroupId = topicGroupId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicGroupTitle() {
        return topicGroupTitle;
    }

    public void setTopicGroupTitle(String topicGroupTitle) {
        this.topicGroupTitle = topicGroupTitle;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Boolean getReviewNeeded() {
        return reviewNeeded;
    }

    public void setReviewNeeded(Boolean reviewNeeded) {
        this.reviewNeeded = reviewNeeded;
    }

    public void setDuration(int hours, int minutes) {
        String duration = calculateLessonDuration(hours, minutes);
        this.duration = duration;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String calculateLessonDuration(int hours, int minutes) {
        String duration;
        if (minutes == 0) {
            duration = hours + "h";
        } else {
            duration = hours + "h" + minutes;
        }

        return duration;
    }

    public String getMonth(String dateToFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String month = null;
        try {
            Date date = dateFormat.parse(dateToFormat);
            month = (String) android.text.format.DateFormat.format("MMMM", date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  month;

    }

    public String getDay(String dateToFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String day = null;
        try {
            Date date = dateFormat.parse(dateToFormat);
            day = (String) android.text.format.DateFormat.format("dd", date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  day;

    }

    public String getDate(String dateToFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String dateFormated = null;
        try {
            Date date = dateFormat.parse(dateToFormat);
            dateFormated = (String) android.text.format.DateFormat.format("dd/MM/yyyy", date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  dateFormated;

    }

    public String getTime(String dateToFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String time = null;
        try {
            Date date = dateFormat.parse(dateToFormat);
            time = (String) android.text.format.DateFormat.format("HH:mm", date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
