package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;
import com.qwerteach.wivi.qwerteachapp.common.Common;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by wivi on 10/01/17.
 */

public class User implements Serializable {

    @SerializedName("id")
    private Integer userId;
    @SerializedName("firstname")
    private String firstName;
    @SerializedName("lastname")
    private String lastName;
    @SerializedName("birthdate")
    private String birthdate;
    @SerializedName("description")
    private String description;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("occupation")
    private String occupation;
    @SerializedName("postulance_accepted")
    private Boolean postulanceAccepted;
    @SerializedName("level_id")
    private Integer levelId;
    @SerializedName("mango_id")
    private Integer mangoId;
    @SerializedName("authentication_token")
    private String token;
    @SerializedName("email")
    private String email;
    @SerializedName("phone_country_code")
    private String phoneCountryCode;
    @SerializedName("time_zone")
    private String timeZone;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("min_price")
    private String minPrice;
    @SerializedName("avg_reviews")
    private String rating;
    @SerializedName("offers")
    private ArrayList<SmallAd> smallAds;
    @SerializedName("reviews")
    private ArrayList<Review> reviews;

    public User() {

    }

    public User(int userId, String firstName, String lastName, String birthdate,
                String occupation, String description) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.occupation = occupation;
        this.description = description;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getDescription() {
        if (description != null) {
            description = description.replace("\\n\\n", "");
            description = description.replace("\\n", "");
            return description;
        } else {
            return "";
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAge() {

        String date = this.birthdate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;

        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    public String getAvatarUrl() {
        return Common.IP_ADDRESS + avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = Common.IP_ADDRESS + avatarUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public Boolean getPostulanceAccepted() {
        return postulanceAccepted;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Integer getMangoId() {
        return mangoId;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }


    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getToken() {
        return token;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getRating() {
        return rating;
    }

    public ArrayList<SmallAd> getSmallAds() {
        return smallAds;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public String getTopics() {
        ArrayList<String> topics = new ArrayList<>();
        for (int i = 0; i < getSmallAds().size(); i++) {
            topics.add(getSmallAds().get(i).getTitle());
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
}

