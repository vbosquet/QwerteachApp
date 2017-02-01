package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by wivi on 10/01/17.
 */

public class User implements Serializable {

    private static final String BASE_URL = "http://192.168.0.103:3000";

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
    @SerializedName("phonenumber")
    private String phoneNumber;
    @SerializedName("occupation")
    private String occupation;
    @SerializedName("postulance_accepted")
    private Boolean postulanceAccepted;
    @SerializedName("level_id")
    private Integer levelId;
    @SerializedName("mango_id")
    private Integer mangoId;
    private String avatarUrl;

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
        return description;
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
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = BASE_URL + avatarUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getPostulanceAccepted() {
        return postulanceAccepted;
    }

    public void setPostulanceAccepted(Boolean postulanceAccepted) {
        this.postulanceAccepted = postulanceAccepted;
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

    public void setMangoId(Integer mangoId) {
        this.mangoId = mangoId;
    }
}
