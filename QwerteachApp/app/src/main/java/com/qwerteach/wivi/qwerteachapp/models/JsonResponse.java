package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by wivi on 19/01/17.
 */

public class JsonResponse {

    @SerializedName("avatar")
    private String avatar;
    @SerializedName("message")
    private String message;
    @SerializedName("success")
    private String success;
    @SerializedName("user")
    private User user;
    @SerializedName("adverts")
    private ArrayList<SmallAd> smallAds;
    @SerializedName("topic_titles")
    private ArrayList<String> topicTitles;
    @SerializedName("advert_prices")
    private ArrayList<ArrayList<SmallAdPrice>> smallAdPrices;
    @SerializedName("reviews")
    private ArrayList<Review> reviews;
    @SerializedName("review_sender_names")
    private ArrayList<String> reviewSenderNames;
    @SerializedName("avg")
    private float rating;
    @SerializedName("min_price")
    private double minPrice;
    @SerializedName("notes")
    private ArrayList<Integer> notes;
    @SerializedName("levels")
    private ArrayList<Level> levels;
    @SerializedName("topic")
    private String topicTitle;
    @SerializedName("topic_group")
    private String topicGroupTitle;
    @SerializedName("topic_groups")
    private ArrayList<TopicGroup> topicGroups;
    @SerializedName("topics")
    private ArrayList<Topic> topics;
    @SerializedName("advert")
    private SmallAd smallAd;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public ArrayList<SmallAd> getSmallAds() {
        return smallAds;
    }

    public void setSmallAds(ArrayList<SmallAd> smallAds) {
        this.smallAds = smallAds;
    }


    public ArrayList<String> getTopicTitles() {
        return topicTitles;
    }

    public void setTopicTitles(ArrayList<String> topicTitles) {
        this.topicTitles = topicTitles;
    }

    public ArrayList<ArrayList<SmallAdPrice>> getSmallAdPrices() {
        return smallAdPrices;
    }

    public void setSmallAdPrices(ArrayList<ArrayList<SmallAdPrice>> smallAdPrices) {
        this.smallAdPrices = smallAdPrices;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<String> getReviewSenderNames() {
        return reviewSenderNames;
    }

    public void setReviewSenderNames(ArrayList<String> reviewSenderNames) {
        this.reviewSenderNames = reviewSenderNames;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public ArrayList<Integer> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Integer> notes) {
        this.notes = notes;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
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

    public ArrayList<TopicGroup> getTopicGroups() {
        return topicGroups;
    }

    public void setTopicGroups(ArrayList<TopicGroup> topicGroups) {
        this.topicGroups = topicGroups;
    }

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Topic> topics) {
        this.topics = topics;
    }

    public SmallAd getSmallAd() {
        return smallAd;
    }

    public void setSmallAd(SmallAd smallAd) {
        this.smallAd = smallAd;
    }
}
