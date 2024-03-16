package com.hemant.ecofoodtrackerapp.models;

public class CartModel {

    String cartFoodId, cartFoodStatus, cartFoodUserId;

    public CartModel() {
    }

    public CartModel(String cartFoodId, String cartFoodStatus, String cartFoodUserId) {
        this.cartFoodId = cartFoodId;
        this.cartFoodStatus = cartFoodStatus;
        this.cartFoodUserId = cartFoodUserId;
    }

    public String getCartFoodId() {
        return cartFoodId;
    }

    public void setCartFoodId(String cartFoodId) {
        this.cartFoodId = cartFoodId;
    }

    public String getCartFoodStatus() {
        return cartFoodStatus;
    }

    public void setCartFoodStatus(String cartFoodStatus) {
        this.cartFoodStatus = cartFoodStatus;
    }

    public String getCartFoodUserId() {
        return cartFoodUserId;
    }

    public void setCartFoodUserId(String cartFoodUserId) {
        this.cartFoodUserId = cartFoodUserId;
    }
}
