package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wivi on 17/11/16.
 */

public class Teacher implements Serializable{

    private ArrayList<String> topicTitleList;
    private ArrayList<Double> priceList;
    private ArrayList<SmallAd> smallAds;
    private ArrayList<Review> reviews;
    private int numberOfReviews;
    private float rating;
    private User user;

    public Teacher() {
        topicTitleList = new ArrayList<>();
        priceList = new ArrayList<>();
        smallAds = new ArrayList<>();
    }

    public String getTopicTitleList() {
        String courseMaterialNames = "";

        for (int i = 0; i < topicTitleList.size(); i++) {

            if (topicTitleList.get(i).equals(topicTitleList.get(topicTitleList.size() - 1))) {
                courseMaterialNames += topicTitleList.get(i);
            } else {
                courseMaterialNames += (topicTitleList.get(i) + ", ");
            }
        }

        return courseMaterialNames;
    }

    public void setTopicTitleList(ArrayList<String> topicTitleList) {
        this.topicTitleList = topicTitleList;
    }

    public Double getMinPrice() {
        if (priceList.size() > 0) {
            return priceList.get(0);
        } else {
            return 0.0;
        }

    }

    public void setPriceList(ArrayList<Double> priceList) {
        this.priceList = priceList;
        Collections.sort(this.priceList);
    }

    public void addPriceToPriceList(ArrayList<Double> prices) {
        for (int i = 0; i < prices.size(); i++) {
            priceList.add(prices.get(i));
        }
        Collections.sort(priceList);
    }

    public ArrayList<Double> getPriceList() {
        return priceList;
    }

    public ArrayList<SmallAd> getSmallAds() {
        return smallAds;
    }

    public void addSmallAds(SmallAd smallAd) {
        this.smallAds.add(smallAd);
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

    public void setSmallAds(ArrayList<SmallAd> smallAds) {
        this.smallAds = smallAds;
    }
}
