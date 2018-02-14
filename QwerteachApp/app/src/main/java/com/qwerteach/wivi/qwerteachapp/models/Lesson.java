package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;
import com.qwerteach.wivi.qwerteachapp.common.Common;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wivi on 7/12/16.
 */

public class Lesson implements Serializable {

    @SerializedName("id")
    private Integer lessonId;
    @SerializedName("student_id")
    private Integer studentId;
    @SerializedName("teacher_id")
    private Integer teacherId;
    @SerializedName("topic_id")
    private Integer topicId;
    @SerializedName("topic_group_id")
    private Integer topicGroupId;
    @SerializedName("level_id")
    private Integer levelId;
    @SerializedName("time_start")
    private String timeStart;
    @SerializedName("time_end")
    private String timeEnd;
    @SerializedName("status")
    private String status;
    @SerializedName("price")
    private String price;
    @SerializedName("comment")
    private String comment;

    private String hours;
    private String minutes;
    private String userName;
    private String topicTitle;
    private String topicGroupTitle;
    private String level;
    private String avatar;
    private List<Payment> payments;

    public Lesson(){

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getHour() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(changeStringToDate(this.getTimeStart()));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return  hour;

    }

    public int getMinute() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(changeStringToDate(this.getTimeStart()));
        int minute = cal.get(Calendar.MINUTE);
        return  minute;

    }

    public int getMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(changeStringToDate(this.getTimeStart()));
        int month = cal.get(Calendar.MONTH);
        return  month;

    }

    public int getDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(changeStringToDate(this.getTimeStart()));
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return  day;

    }

    public int getYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(changeStringToDate(this.getTimeStart()));
        int year = cal.get(Calendar.YEAR);
        return  year;

    }

    public String getDate() {
        String dateFormated = (String) android.text.format.DateFormat.format("dd/MM/yyyy", changeStringToDate(this.getTimeStart()));
        return  dateFormated;

    }

    public String getTime() {
        String time = (String) android.text.format.DateFormat.format("HH:mm", changeStringToDate(this.getTimeStart()));
        return time;
    }

    public String calculateLessonDuration() {
        Date lessonStart = changeStringToDate(this.getTimeStart());
        Date lessonEnd = changeStringToDate(this.getTimeEnd());
        long d = lessonEnd.getTime() - lessonStart.getTime();
        int minutes = (int) ((d % 3600000) / 60000);
        int hours = (int) (d/ 3600000);

        String duration;
        if (minutes == 0) {
            duration = hours + "h";
        } else if (hours < 1) {
            duration = minutes + "min";
        } else {
            duration = hours + "h" + minutes;
        }

        return duration;
    }

    private Date changeStringToDate(String string) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = Common.IP_ADDRESS + avatar;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
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

    public boolean pending(User user) {
        boolean pending;
        pending = (user.getPostulanceAccepted() && this.getStatus().equals("pending_teacher"))
                || (!user.getPostulanceAccepted() && this.getStatus().equals("pending_student"));

        return pending;
    }
}
