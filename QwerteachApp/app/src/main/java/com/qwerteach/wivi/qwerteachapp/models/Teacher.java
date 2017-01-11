package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wivi on 17/11/16.
 */

public class Teacher implements Serializable{

    private ArrayList<SmallAd> smallAds;
    private ArrayList<Review> reviews;
    private int numberOfReviews;
    private float rating;
    private User user;
    private double minPrice;

    public Teacher() {
    }

    public String getTopics() {
        ArrayList<String> topics = new ArrayList<>();
        for (int i = 0; i < smallAds.size(); i++) {
            topics.add(smallAds.get(i).getTitle());
        }

        String courseMaterialNames = "";

        for (int i = 0; i < topics.size(); i++) {

            if (topics.get(i).equals(topics.get(topics.size() - 1))) {
                courseMaterialNames += topics.get(i);
            } else {
                courseMaterialNames += (topics.get(i) + ", ");
            }
        }

        return courseMaterialNames;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<SmallAd> getSmallAds() {
        return smallAds;
    }

    public void setSmallAds(ArrayList<SmallAd> smallAds) {
        this.smallAds = smallAds;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }
}
