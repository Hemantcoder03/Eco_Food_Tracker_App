package com.hemant.ecofoodtrackerapp.models;


import com.google.firebase.Timestamp;

public class UserDataModel {

    public String userName, userEmail, userId, userPhone, userAddress;
    public Timestamp createdTimeStamp;

    public UserDataModel() {
    }

    public UserDataModel(String userName, String userEmail, String userId, String userPhone, String userAddress, Timestamp createdTimeStamp) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }
}
