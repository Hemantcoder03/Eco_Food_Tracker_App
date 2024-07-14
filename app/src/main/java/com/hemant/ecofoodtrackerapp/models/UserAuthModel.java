package com.hemant.ecofoodtrackerapp.models;

public class UserAuthModel {

    String email, password;

    public UserAuthModel() {
    }

    public UserAuthModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
