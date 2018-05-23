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
    @SerializedName("offers")
    private ArrayList<SmallAd> smallAds;
    @SerializedName("topic_titles")
    private ArrayList<String> topicTitles;
    @SerializedName("offer_prices")
    private ArrayList<ArrayList<SmallAdPrice>> smallAdPrices;
    @SerializedName("reviews")
    private ArrayList<Review> reviews;
    @SerializedName("review_sender_names")
    private ArrayList<String> reviewSenderNames;
    @SerializedName("avg")
    private float rating;
    @SerializedName("avgs")
    private List<Float> ratings;
    @SerializedName("min_price")
    private double minPrice;
    @SerializedName("notes")
    private ArrayList<Integer> notes;
    @SerializedName("levels")
    private ArrayList<Level> levels;
    @SerializedName("topic")
    private String topicTitle;
    @SerializedName("topic_group")
    private TopicGroup topicGroup;
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
    @SerializedName("lessons")
    private ArrayList<Lesson> lessons;
    @SerializedName("payment_status")
    private String paymentStatus;
    @SerializedName("expired")
    private boolean expired;
    @SerializedName("past")
    private boolean past;
    @SerializedName("duration")
    private Duration duration;
    @SerializedName("name")
    private String userName;
    @SerializedName("lesson")
    private Lesson lesson;
    @SerializedName("upcoming_lessons")
    private List<Lesson> upcomingLessons;
    @SerializedName("to_do_list")
    private List<Lesson> toDoList;
    @SerializedName("past_lessons")
    private List<Lesson> pastLessons;
    @SerializedName("past_lessons_given")
    private List<Lesson> pastLessonsGiven;
    @SerializedName("to_unlock_lessons")
    private List<Lesson> toUnlockLessons;
    @SerializedName("to_review_lessons")
    private List<Lesson> toReviewLessons;
    @SerializedName("lesson_status")
    private String lessonStatus;
    @SerializedName("messages")
    private List<Message> messages;
    @SerializedName("conversations")
    private List<Conversation> conversations;
    @SerializedName("recipients")
    private List<User> recipients;
    @SerializedName("avatars")
    private List<String> avatars;
    @SerializedName("last_message")
    private Message lastMessage;
    @SerializedName("participant_avatars")
    private List<String> participantAvatars;
    @SerializedName("author_names")
    private ArrayList<String> transactionAuthorNames;
    @SerializedName("credited_user_names")
    private ArrayList<String> transactionCreditedUserNames;
    @SerializedName("bank_wire")
    private BankWireData bankWireData;
    @SerializedName("errors")
    private List<String> errorMessages;
    @SerializedName("error")
    private String errorMesage;
    @SerializedName("payments")
    private List<Payment> payments;
    @SerializedName("price")
    private Float lessonTotalPrice;
    @SerializedName("transaction_infos")
    private List<String> transactionInfos;
    @SerializedName("notifications")
    private List<Notification> notifications;
    @SerializedName("review_needed")
    private Boolean reviewNeeded;
    @SerializedName("transaction")
    private  Transaction transaction;
    @SerializedName("client_id")
    private String clientId;

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

    public TopicGroup getTopicGroup() {
        return topicGroup;
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

    public Duration getDuration() {
        return duration;
    }

    public String getUserName() {
        return userName;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public List<Lesson> getUpcomingLessons() {
        return upcomingLessons;
    }

    public List<Lesson> getToDoList() {
        return toDoList;
    }

    public String getLessonStatus() {
        return lessonStatus;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public List<String> getAvatars() {
        return avatars;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public List<String> getParticipantAvatars() {
        return participantAvatars;
    }

    public ArrayList<String> getTransactionAuthorNames() {
        return transactionAuthorNames;
    }

    public ArrayList<String> getTransactionCreditedUserNames() {
        return transactionCreditedUserNames;
    }

    public BankWireData getBankWireData() {
        return bankWireData;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public String getErrorMesage() {
        return errorMesage;
    }

    public List<Float> getRatings() {
        return ratings;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public List<Lesson> getPastLessons() {
        return pastLessons;
    }

    public Float getLessonTotalPrice() {
        return lessonTotalPrice;
    }

    public List<String> getTransactionInfos() {
        return transactionInfos;
    }

    public List<Lesson> getPastLessonsGiven() {
        return pastLessonsGiven;
    }

    public List<Lesson> getToUnlockLessons() {
        return toUnlockLessons;
    }

    public List<Lesson> getToReviewLessons() {
        return toReviewLessons;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public Boolean getReviewNeeded() {
        return reviewNeeded;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getClientId() {
        return clientId;
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
