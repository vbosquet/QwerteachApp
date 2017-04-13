package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wivi on 10/04/17.
 */

public class Payment implements Serializable {

    @SerializedName("id")
    private Integer paymentId;
    @SerializedName("price")
    private Float price;
    @SerializedName("status")
    private String status;
    @SerializedName("lesson_id")
    private Integer lessonId;

    public Payment() {

    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getStatus() {
        String status = null;
        switch (this.status) {
            case "locked":
                status = "Payé - détenu par Qwerteach";
                break;
            case "paid":
                status = "Payé - versé au professeur";
                break;
            case "pending":
                status = "A payer";
                break;
            case "canceled":
                status = "Annulé";
                break;
            case "disputed":
                status = "En litige";
                break;
            case "refunded":
                status = "Remboursé";
                break;
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLessonId() {
        return lessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }
}
