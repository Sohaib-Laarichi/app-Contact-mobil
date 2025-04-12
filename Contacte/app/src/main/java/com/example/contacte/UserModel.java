package com.example.contacte;

import com.google.gson.annotations.SerializedName;

public class UserModel {

    @SerializedName("name")
    private String name;

    @SerializedName("number")
    private String number;

    @SerializedName("email")
    private String email;

    @SerializedName("imei")
    private String imei;

    public UserModel(String name, String number, String email, String imei) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.imei = imei;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public String getImei() {
        return imei;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
