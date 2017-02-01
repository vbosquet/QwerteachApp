package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wivi on 17/01/17.
 */

public class UserBankAccount implements Serializable {

    @SerializedName("iban")
    private String iban;
    @SerializedName("bic")
    private String bic;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("owner_name")
    private String ownerName;
    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type;
    @SerializedName("account_number")
    private String accountNumber;
    @SerializedName("sort_code")
    private String sortCode;
    @SerializedName("aba")
    private String aba;
    @SerializedName("deposit_account_type")
    private String depositAccountType;
    @SerializedName("bank_name")
    private String bankName;
    @SerializedName("institution_number")
    private String institutionNumber;
    @SerializedName("branch_code")
    private String branchCode;
    @SerializedName("country")
    private String country;

    public UserBankAccount() {

    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAba() {
        return aba;
    }

    public void setAba(String aba) {
        this.aba = aba;
    }

    public String getDepositAccountType() {
        return depositAccountType;
    }

    public void setDepositAccountType(String depositAccountType) {
        this.depositAccountType = depositAccountType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getInstitutionNumber() {
        return institutionNumber;
    }

    public void setInstitutionNumber(String institutionNumber) {
        this.institutionNumber = institutionNumber;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
