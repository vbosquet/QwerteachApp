package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wivi on 21/03/17.
 */

public class BankWireData implements Serializable {

    @SerializedName("bank_account")
    private BankAccount bankAccount;
    @SerializedName("wire_reference")
    private String wireReference;

    public BankWireData() {

    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public String getWireReference() {
        return wireReference;
    }

    public static class BankAccount implements Serializable {
        @SerializedName("bic")
        private String bic;
        @SerializedName("iban")
        private String iban;
        @SerializedName("owner_name")
        private String ownerName;
        @SerializedName("owner_address")
        private OwnerAddress ownerAddress;

        public BankAccount() {

        }

        public String getBic() {
            return bic;
        }

        public String getIban() {
            return iban;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public OwnerAddress getOwnerAddress() {
            return ownerAddress;
        }

        public String getAddress() {
            String address = "";

            if (ownerAddress.getAddressLine1() != null) {
                address += (ownerAddress.getAddressLine1() + ", ");
            }

            if (ownerAddress.getAddressLine2() != null) {
                address += (ownerAddress.getAddressLine2() + ", ");
            }

            if (ownerAddress.getCity() != null) {
                address += (ownerAddress.getCity() + ", ");
            }

            if (ownerAddress.getPostalCode() != null) {
                address += (ownerAddress.getPostalCode() + ", ");
            }

            if (ownerAddress.getCountry() != null) {
                address += (ownerAddress.getCountry());
            }

            return address;

        }

    }

    public static class OwnerAddress {
        @SerializedName("address_line1")
        String addressLine1;
        @SerializedName("address_line2")
        String addressLine2;
        @SerializedName("country")
        String country;
        @SerializedName("city")
        String city;
        @SerializedName("postal_code")
        String postalCode;
        @SerializedName("region")
        String region;

        public OwnerAddress(){

        }

        public String getAddressLine1() {
            return addressLine1;
        }

        public String getAddressLine2() {
            return addressLine2;
        }

        public String getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getRegion() {
            return region;
        }
    }
}
