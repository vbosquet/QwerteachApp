package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    @SerializedName("level")
    private String levelTitle;
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
    @SerializedName("url")
    private String url;
    @SerializedName("account")
    private UserWalletInfos userWalletInfos;
    @SerializedName("bank_accounts")
    private ArrayList<UserBankAccount> bankAccounts;
    @SerializedName("transactions")
    private ArrayList<Transaction> transactions;
    @SerializedName("author")
    private String transactionAuthorName;
    @SerializedName("credited_user")
    private String transactionCreditedUserName;
    @SerializedName("lessons")
    private ArrayList<Lesson> lessons;
    @SerializedName("payment_status")
    private String paymentStatus;
    @SerializedName("expired")
    private boolean expired;
    @SerializedName("past")
    private boolean past;
    @SerializedName("review_needed")
    private boolean reviewNeed;
    @SerializedName("duration")
    private Duration duration;
    @SerializedName("name")
    private String userName;
    @SerializedName("lesson")
    private Lesson lesson;
    @SerializedName("upcoming_lessons")
    private List<Lesson> upcomingLesson;
    @SerializedName("to_do_list")
    private List<Lesson> toDoList;
    @SerializedName("review_asked")
    private List<User> teachersToReview;
    @SerializedName("lesson_status")
    private String lessonStatus;


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

    public String getUrl() {
        return url;
    }

    public UserWalletInfos getUserWalletInfos() {
        return userWalletInfos;
    }

    public ArrayList<UserBankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public String getTransactionAuthorName() {
        return transactionAuthorName;
    }

    public String getTransactionCreditedUserName() {
        return transactionCreditedUserName;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public String getLevelTitle() {
        return levelTitle;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public boolean isExpired() {
        return expired;
    }

    public boolean isPast() {
        return past;
    }

    public boolean isReviewNeed() {
        return reviewNeed;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getUserName() {
        return userName;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public List<Lesson> getUpcomingLesson() {
        return upcomingLesson;
    }

    public List<Lesson> getToDoList() {
        return toDoList;
    }

    public String getLessonStatus() {
        return lessonStatus;
    }

    public List<User> getTeachersToReview() {
        return teachersToReview;
    }

    public static class Duration
    {
        @SerializedName("hours")
        private int hours;
        @SerializedName("minutes")
        private int minutes;

        public Duration() {

        }

        public int getHours() {
            return hours;
        }

        public int getMinutes() {
            return minutes;
        }
    }
}
