package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wivi on 6/01/17.
 */

public class Review implements Serializable {

    @SerializedName("id")
    private Integer reviewId;
    @SerializedName("sender_id")
    private Integer senderId;
    @SerializedName("subject_id")
    private Integer subjectId;
    @SerializedName("review_text")
    private String reviewText;
    @SerializedName("note")
    private Integer note;
    @SerializedName("created_at")
    private String creationDate;
    private String senderFirstName;

    public Review() {

    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public void setSenderFirstName(String senderFirstName) {
        this.senderFirstName = senderFirstName;
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

    public String getYear(String dateToFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String year = null;
        try {
            Date date = dateFormat.parse(dateToFormat);
            year = (String) android.text.format.DateFormat.format("yyyy", date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  year;

    }
}
