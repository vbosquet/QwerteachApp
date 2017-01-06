package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wivi on 6/01/17.
 */

public class Review implements Serializable{
    private int reviewId;
    private int senderId;
    private int subjectId;
    private String reviewText;
    private int note;
    private String creationDate;
    private String senderFirstName;

    public Review() {

    }

    public Review(int reviewId, int senderId, int subjectId, String reviewText, int note, String creationDate) {
        this.reviewId = reviewId;
        this.senderId = senderId;
        this.subjectId = subjectId;
        this.reviewText = reviewText;
        this.note = note;
        this.creationDate = creationDate;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
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
