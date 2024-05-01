package com.hemant.ecofoodtrackerapp.models;

import com.google.firebase.Timestamp;

public class FoodOrderModel {

    String foodId, donorId, orderId, orderUserId, deliveryType, deliveryCharge, deliveryAddress, status;
    Timestamp orderTime;
    public FoodOrderModel(){}

    public FoodOrderModel(String foodId, String donorId, String orderId, String orderUserId, Timestamp orderTime, String deliveryType, String deliveryCharge, String deliveryAddress, String status) {
        this.foodId = foodId;
        this.donorId = donorId;
        this.orderId = orderId;
        this.orderUserId = orderUserId;
        this.orderTime = orderTime;
        this.deliveryType = deliveryType;
        this.deliveryCharge = deliveryCharge;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getDonorId() {
        return donorId;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderUserId() {
        return orderUserId;
    }

    public void setOrderUserId(String orderUserId) {
        this.orderUserId = orderUserId;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
