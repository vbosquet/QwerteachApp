package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

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
    @SerializedName("user_cards")
    private ArrayList<UserCreditCard> userCreditCards;
    @SerializedName("card_registration")
    private CardRegistrationData cardRegistrationData;
    @SerializedName("total_wallet")
    private Integer totalWallet;
    @SerializedName("pagin")
    private ArrayList<User> users;
    @SerializedName("options")
    private ArrayList<ArrayList<String>> options;

    public String getAvatar() {
        return avatar;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getSuccess() {
        return success;
    }

    public ArrayList<SmallAd> getSmallAds() {
        return smallAds;
    }

    public ArrayList<String> getTopicTitles() {
        return topicTitles;
    }

    public ArrayList<ArrayList<SmallAdPrice>> getSmallAdPrices() {
        return smallAdPrices;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public ArrayList<String> getReviewSenderNames() {
        return reviewSenderNames;
    }

    public float getRating() {
        return rating;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public ArrayList<Integer> getNotes() {
        return notes;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public String getTopicGroupTitle() {
        return topicGroupTitle;
    }

    public ArrayList<TopicGroup> getTopicGroups() {
        return topicGroups;
    }

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    public ArrayList<UserCreditCard> getUserCreditCards() {
        return userCreditCards;
    }

    public CardRegistrationData getCardRegistrationData() {
        return cardRegistrationData;
    }

    public Integer getTotalWallet() {
        return totalWallet;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<ArrayList<String>> getOptions() {
        return options;
    }
}
