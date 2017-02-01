package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wivi on 31/01/17.
 */

public class UserWalletInfos implements Serializable {

    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("address_line1")
    private String address;
    @SerializedName("address_line2")
    private String streetNumber;
    @SerializedName("postal_code")
    private String postalCode;
    @SerializedName("city")
    private String city;
    @SerializedName("region")
    private String region;
    @SerializedName("country")
    private String countryCode;
    @SerializedName("nationality")
    private String nationalityCode;
    @SerializedName("country_of_residence")
    private String residencePlaceCode;
    @SerializedName("user")
    private User user;

    public UserWalletInfos() {

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    public String getResidencePlaceCode() {
        return residencePlaceCode;
    }

    public void setResidencePlaceCode(String residencePlaceCode) {
        this.residencePlaceCode = residencePlaceCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
