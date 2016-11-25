package com.qwerteach.wivi.qwerteachapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wivi on 17/11/16.
 */

public class Teacher implements Serializable{

    private int teacherId;
    private String firstName;
    private String lastName;
    private String description;
    private String occupation;
    private String birthDate;
    private ArrayList<String> topicTitleList;
    private ArrayList<Double> priceList;
    private ArrayList<SmallAd> smallAds;

    public Teacher(int id, String firstName, String lastName, String description, String occupation, String birthDate) {
        this.teacherId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.occupation = occupation;
        this.birthDate = birthDate;
        topicTitleList = new ArrayList<>();
        priceList = new ArrayList<>();
        smallAds = new ArrayList<>();
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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
}
